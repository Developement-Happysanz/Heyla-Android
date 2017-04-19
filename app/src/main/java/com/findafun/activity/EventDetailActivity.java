package com.findafun.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.findafun.AppRate.AppRate;
import com.findafun.R;
import com.findafun.app.AppController;
import com.findafun.bean.events.Event;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.helper.FindAFunHelper;
import com.findafun.photowidget.ImageInfo;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.servicehelpers.ShareServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.twitter.TwitterAuthenticateTask;
import com.findafun.twitter.TwitterUtil;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.store.Store;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class EventDetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, IGamificationServiceListener, IEventServiceListener {

    private Animator zoomCurrentAnimator;

    private int zoomShortAnimationDuration;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private Animation.AnimationListener mAnimationListener;
    private Animator mCurrentAnimator;
    private static final String TAG = EventDetailActivity.class.getName();
    private Event event;
    private TextView txtEventName, txtEventCategory, txtEventDesc, txtEventVenue, txtEventStartDate, txtEventEndDate, txtEventStartTime, txtEventEndTime;
    private TextView txtEventTime, txtEventDate, txtEventEntry, txtEventContact, txtEventEmail, txtWebSite;
    private TextView txtViewMore, txtViewLess;
    private ViewFlipper imgEventBanner;
    LinearLayout count_layout;
    int count = 0;
    static TextView page_text[];
    ImageView banner_image_one, banner_image_two, banner_image_three, banner_image_four, banner_image_five;
    View banner_zoom_image_one, banner_zoom_image_two, banner_zoom_image_three;
    //  private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
    ImageLoader uImageLoader = AppController.getInstance().getUniversalImageLoader();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double currentLatitude, currentLongitude;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private boolean isPermissionGranted = false;
    private ImageView mFacebookBtn, mTwitterBtn, mWhatsAppBtn;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private EventServiceHelper mServiceHelper = null;
    private ArrayAdapter<String> mShareAdapter = null;
    private List<String> mShareLIst = new ArrayList<String>();
    private List<Integer> mShareResources = new ArrayList<Integer>();
    private List<Integer> mShareBagroundResources = new ArrayList<Integer>();
    private ArrayList<String> imgList = new ArrayList<>();
    private ArrayList<ImageInfo> imgImageInfos = new ArrayList<>();
    int curRate;
    private int mShortAnimationDuration;
    Button whishListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.activity_event_detail_new);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("EVENT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        curRate = getPreferCount();
        event = (Event) getIntent().getSerializableExtra("eventObj");
        initializeViews();
        populateData();
        if (curRate == 20) {
            fetchAppRate();
        }
        int nVersion = getAppVersion(getApplication());
        Log.d("test", String.valueOf(nVersion));
        getAppUpdate(nVersion);
        clickCountEvent();

    }

    public void getAppUpdate(int nVersion) {

        int versionCode = nVersion;
        final SharedPreferences saving = getSharedPreferences("AppUpdate", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = saving.edit();
        editor.putInt("update", versionCode);
        editor.commit();
        int version = saving.getInt("update", 0);
        if (version < nVersion) {
            version++;
            //   Toast.makeText(this, "Hello There " + test, Toast.LENGTH_SHORT).show();
            editor.putInt("update", version);
            editor.commit();
            UpdateChecker checker = new UpdateChecker(this);
            UpdateChecker.setStore(Store.GOOGLE_PLAY);
            checker.showDialog("test");
            //checker.setSuccessfulChecksRequired(5);
            UpdateChecker.start();
            Log.d("version code", String.valueOf(version));
        } else if (version == nVersion) {
            editor.putInt("update", version);
            editor.commit();
            Log.d("version code", String.valueOf(version));
        }

    }


    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void fetchAppRate() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("Rate " + getApplicationInfo().loadLabel(getPackageManager()).toString())
                .setMessage("If You Enjoy Using Heyla, please take a moment to rate it. Thanks for Your Support!")
                .setPositiveButton("Rate it! ", null)
                .setNegativeButton("No Thanks", null)
                .setNeutralButton("Remind Me Later", null);

        new AppRate(this)
                .setCustomDialog(builder)
                .init();
    }


    private int getPreferCount() {
        int test = 0;
        final SharedPreferences saving = getSharedPreferences("AppRatePref", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = saving.edit();

        test = saving.getInt("rate", 0);

        if (test > 2) {
            test = 0;
            editor.putInt("rate", test);
            editor.commit();
        } else {
            test++;
            //   Toast.makeText(this, "Hello There " + test, Toast.LENGTH_SHORT).show();
            editor.putInt("rate", test);
            editor.commit();
        }
        return test;
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if (mGoogleApiClient != null)
            mGoogleApiClient.connect();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
       /* if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();*/
    }

    /* private void addEvent() {
         Intent intent = new Intent(Intent.ACTION_INSERT)
                 .setData(CalendarContract.Events.CONTENT_URI)
                 .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,(long) event.getStartDate())
                 .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, (long) event.getEndDate())
                 .putExtra(CalendarContract.Events.TITLE, "")
                 .putExtra(CalendarContract.Events.DESCRIPTION, "")
                 .putExtra(CalendarContract.Events.EVENT_LOCATION, "")
                 .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

         startActivity(intent);
     }*/
    public void facebook(View v) {
        try {

            Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
            if (intent != null) {
                // The application exists
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.facebook.katana");

                shareIntent.putExtra(Intent.EXTRA_TITLE, event.getEventName());
                shareIntent.putExtra(Intent.EXTRA_TEXT, event.getDescription());
                // Start the specific social application
                startActivity(shareIntent);
            } else {
                // The application does not exist
                // Open GooglePlay or use the default system picker
            }
        } catch (Exception ex) {
            return;
        }
    }

    public void twitter(View v) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.twitter.android");
            if (intent != null) {
                // The application exists
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.twitter.android");
                SpannableString content = new SpannableString("http://www.heylaapp.com/");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                String text = event.getEventName();
                shareIntent.putExtra(Intent.EXTRA_TITLE, "www.heylaapp.com");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                // Start the specific social application
                startActivity(shareIntent);
            } else {
                // The application does not exist
                // Open GooglePlay or use the default system picker
            }
        } catch (Exception ex) {
            return;
        }
    }

    public void whatsapp(View v) {

        PackageManager pm = getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            SpannableString content = new SpannableString("http://www.heylaapp.com/");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            String text = event.getEventName() + "\n" + event.getDescription() + "\n" + content;
            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
            sendShareStatus();
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void populateData() {

        txtEventName.setText(event.getEventName());
        // txtEventDesc.setText(event.getDescription());
        if (event.getDescription() != null) {
            txtEventCategory.setText(event.getDescription());
            txtViewMore.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    txtViewMore.setVisibility(View.GONE);
                    txtViewLess.setVisibility(View.VISIBLE);
                    txtEventCategory.setMaxLines(Integer.MAX_VALUE);

                }
            });

            txtViewLess.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    txtViewLess.setVisibility(View.GONE);
                    txtViewMore.setVisibility(View.VISIBLE);
                    txtEventCategory.setMaxLines(2);

                }
            });

        } else {
            txtViewMore.setVisibility(View.GONE);

        }

        txtEventVenue.setText(event.getEventAddress());

        txtEventVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = Double.parseDouble(event.getEventLatitude());
                double longitude = Double.parseDouble(event.getEventLongitude());
                if ((lat > 0) | (longitude > 0)) {
                    Intent intent = new Intent(EventDetailActivity.this, MapsActivity.class);
                    intent.putExtra("eventObj", event);
                    startActivity(intent);
                } else {
                    Toast.makeText(EventDetailActivity.this, "Location information not available for this event", Toast.LENGTH_SHORT).show();
                }
            }
        });
        String timeDetails = null;
        String dateDetails = null;
        String start = FindAFunHelper.getDate(event.getStartDate());
        String end = FindAFunHelper.getDate(event.getEndDate());
        if (start != null) {
            txtEventStartDate.setText(start);
        }
        if ((end != null)) {
            txtEventEndDate.setText(end);
        }
        if (event.getEvent_cost() != null) {
            txtEventEntry.setText(event.getEvent_cost());
        }
        //fetch timer values
        start = FindAFunHelper.getTime(event.getStartDate());
        end = FindAFunHelper.getTime(event.getEndDate());

        String startTime = "", endTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date startDateObj = sdf.parse(start);
            final Date endDateObj = sdf.parse(end);
            System.out.println(startDateObj);
            startTime = (new SimpleDateFormat("hh:mm a").format(startDateObj));
            endTime = (new SimpleDateFormat("hh:mm a").format(endDateObj));
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        if (start != null) {
            txtEventStartTime.setText(startTime);
        }

        if (end != null) {
            txtEventEndTime.setText(endTime);
        }

        if ((event.getEventEmail() != null) && !(event.getEventEmail().isEmpty())) {
            txtEventEmail.setText(event.getEventEmail());
        } else {
            txtEventEmail.setText("N/A");
        }
        Log.d(TAG, "Image uri is" + event.getEventBanner());
        //  uImageLoader.displayImage((event.getEventLogo()), imgEventBanner);
        if (event.getEventLogo().contains(".")) {
            uImageLoader.displayImage(event.getEventLogo(), banner_image_one);
        }
        if (event.getEventLogo_1().contains(".")) {
            uImageLoader.displayImage(event.getEventLogo_1(), banner_image_two);
            imgEventBanner.startFlipping();
        } else {
            banner_image_two.setVisibility(View.GONE);
            imgEventBanner.stopFlipping();
        }
        if (event.getEventLogo_2().contains(".")) {
            uImageLoader.displayImage(event.getEventLogo_2(), banner_image_three);
        } else {
            banner_image_three.setVisibility(View.GONE);
        }
        //  uImageLoader.displayImage("http://placehold.it/120x120&text=image4",banner_image_four);
        //   uImageLoader.displayImage("http://placehold.it/120x120&text=image5",banner_image_five);
        imgList.add(0, event.getEventLogo());
        imgList.add(1, "http://placehold.it/120x120&text=image2");
        imgList.add(2, "http://placehold.it/120x120&text=image3");

        mShareLIst.add("Facebook");
        mShareLIst.add("Twitter");
        mShareLIst.add("WhatsApp");
        mShareResources.add(R.drawable.facebook);
        mShareResources.add(R.drawable.twitter);
        mShareResources.add(R.drawable.whatsapp);
        initializeViews();
    }

    private void showShareList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        //  builderSingle.setTitle("Share Event Using");
        builderSingle.setTitle("Share the world using");
        /*View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Gender");
        builderSingle.setCustomTitle(view);*/

        builderSingle.setAdapter(mShareAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mShareLIst.get(which);
                        if (which == 0) {
                            shareWithfacebook();

                        } else if (which == 1) {
                            postUsingTwitter();
                        } else if (which == 2) {
                            whatsapp(null);
                        }

                    }
                });
        builderSingle.show();
    }


    private void initializeViews() {

        banner_zoom_image_one = (ImageView) findViewById(R.id.banner_image_one);
        banner_zoom_image_two = (ImageView) findViewById(R.id.banner_image_two);
        banner_zoom_image_three = (ImageView) findViewById(R.id.banner_image_three);
        banner_image_one = (ImageView) findViewById(R.id.banner_image_one);
        // banner_image_one.set
        banner_image_two = (ImageView) findViewById(R.id.banner_image_two);
        banner_image_three = (ImageView) findViewById(R.id.banner_image_three);
        /*banner_image_four = (ImageView) findViewById(R.id.banner_image_four);
        banner_image_five = (ImageView) findViewById(R.id.banner_image_five);*/
        count_layout = (LinearLayout) findViewById(R.id.image_count);
        txtViewMore = (TextView) findViewById(R.id.seemore);
        txtViewLess = (TextView) findViewById(R.id.seeless);
        txtEventName = (TextView) findViewById(R.id.txt_event_name);
        // txtEventDesc = (TextView) findViewById(R.id.txt_event_desc);
        txtEventVenue = (TextView) findViewById(R.id.txt_event_venue);
        txtEventCategory = (TextView) findViewById(R.id.txt_event_category);
        //txtEventTime = (TextView) findViewById(R.id.txt_calander);
        //txtEventDate = (TextView) findViewById(R.id.txt_time);
        txtEventEntry = (TextView) findViewById(R.id.txt_event_entry);
        txtEventEmail = (TextView) findViewById(R.id.txt_contact_mail);
        //txtWebSite = (TextView) findViewById(R.id.txt_website);

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        whishListBtn = (Button) findViewById(R.id.whishlist_btn);
        LinearLayout shareBtn = (LinearLayout) findViewById(R.id.share_btn);
        LinearLayout contactBtn = (LinearLayout) findViewById(R.id.contact_btn);
        LinearLayout engageBtn = (LinearLayout) findViewById(R.id.engage_btn);
        Button bookingBtn = (Button) findViewById(R.id.booking_btn);
        Switch checkinsBtn = (Switch) findViewById(R.id.checkins_btn);

        String bookingStatus = event.getEventBookingStatus();

        if (bookingStatus.equalsIgnoreCase("1")) {
            bookingBtn.setVisibility(View.VISIBLE);
        } else {
            bookingBtn.setVisibility(View.GONE);
        }

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        whishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {
                    Log.d(TAG, "Bookmark Button selected" + event.getId());
                    //        getCalender();
                    if (mServiceHelper == null) {
                        mServiceHelper = new EventServiceHelper(EventDetailActivity.this);
                        mServiceHelper.setEventServiceListener(EventDetailActivity.this);
                    }
                    if (GamificationDataHolder.getInstance().isEventBookmarked(event.getId())) {
                        Toast.makeText(EventDetailActivity.this, "Event already bookmarked", Toast.LENGTH_SHORT).show();
                        int imgResource = R.drawable.ic_wishlist1_selected;
                        whishListBtn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    } else {
                        try {
                            mServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.ADD_EVENT_BOOKMARK,
                                    Integer.parseInt(PreferenceStorage.getUserId(EventDetailActivity.this)), Integer.parseInt((event.getId()))));

                            int imgResource = R.drawable.ic_wishlist1_selected;
                            whishListBtn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EventDetailActivity.this);
                    alertDialogBuilder.setTitle("Login");
                    alertDialogBuilder.setMessage("Log in to Access");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    doLogout();
                                }
                            });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialogBuilder.show();
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {

                    showShareList();
                }
                else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EventDetailActivity.this);
                    alertDialogBuilder.setTitle("Login");
                    alertDialogBuilder.setMessage("Log in to Access");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            doLogout();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                }


            }
        });

        engageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            EventDetailActivity.this);
                    builder.setTitle("Events");
                    builder.setMessage("Making your presense for the event?");
                    builder.setPositiveButton("May Be",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Toast.makeText(getApplicationContext(), "Thanks for your interest.", Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.setNegativeButton("Not Now",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Toast.makeText(getApplicationContext(), "Wish to see you soon.", Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.show();
                    sendShareStatusUserActivity(3);

                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EventDetailActivity.this);
                    alertDialogBuilder.setTitle("Login");
                    alertDialogBuilder.setMessage("Log in to Access");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            doLogout();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                }

            }
        });

        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {

                    String bookingStatus = event.getEventBookingStatus();

                    if (bookingStatus.equalsIgnoreCase("1")) {

                        // Toast.makeText(getApplicationContext(), "Book this event", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), BookingPlansActivity.class);
                        intent.putExtra("eventId", event.getId());
                        intent.putExtra("eventName", event.getEventName());
                        intent.putExtra("eventVenue", event.getEventVenue());
                        String start = FindAFunHelper.getDate(event.getStartDate());
                        String end = FindAFunHelper.getDate(event.getEndDate());
                        intent.putExtra("eventStartEndDate", start + " - " + end);

                        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } else {
                        // Toast.makeText(getApplicationContext(),"Booking is currently not available for this event"+event.getEventName().toString(),Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Booking is currently not available for this event - " + event.getEventName().toString() + "\nPlease try later.", Toast.LENGTH_LONG).show();

                        // sendShareStatusUserActivity(4);
                    }
                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EventDetailActivity.this);
                    alertDialogBuilder.setTitle("Login");
                    alertDialogBuilder.setMessage("Log in to Access");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            doLogout();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                }
            }
        });

        checkinsBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "You have successfully checked-in for the event - " + event.getEventName().toString() + "\nGet ready for the fun! ", Toast.LENGTH_LONG).show();
                    } else {

                    }

                    sendShareStatusUserActivity(2);
                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EventDetailActivity.this);
                    alertDialogBuilder.setTitle("Login");
                    alertDialogBuilder.setMessage("Log in to Access");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            doLogout();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                }
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String phoneNumber = event.getContact();
                if ((phoneNumber != null) && !(phoneNumber.isEmpty())) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            EventDetailActivity.this);
                    builder.setTitle("Any queries? Feel free to call us.");
                    builder.setMessage(" " + phoneNumber + " ");

                    builder.setPositiveButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    dialog.dismiss();
                                }
                            });
                    builder.setNegativeButton("Call",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    startActivity(intent);
                                }
                            });

                    builder.show();

                }

            }
        });

        txtEventStartDate = (TextView) findViewById(R.id.txt_calander);
        txtEventEndDate = (TextView) findViewById(R.id.cal_to_val);
        txtEventStartTime = (TextView) findViewById(R.id.txt_clock_from_val);
        txtEventEndTime = (TextView) findViewById(R.id.clock_to_val);
        imgEventBanner = (ViewFlipper) findViewById(R.id.banner_one);
        imgEventBanner.isAutoStart();
        /*used for flipping banner*/
        //  imgList.add(0, event.getEventLogo());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(EventDetailActivity.this));
        //  BannerAdapter adapter = new BannerAdapter(this, imgList);
        //*used for banner Touch Event*//*
    /*    imgEventBanner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
              //  new BannerAdapter(getApplicationContext(), imgList);
               //  detector.onTouchEvent(event);
                return true;
            }
        });*/
        //  imgEventBanner.setAdapter(adapter);
        count = 5; //imgEventBanner.getAdapter().getCount();
        page_text = new TextView[count];
        for (int i = 0; i < count; i++) {
            page_text[i] = new TextView(this);
            page_text[i].setText(".");
            page_text[i].setTextSize(45);
            page_text[i].setTypeface(null, Typeface.BOLD);
            page_text[i].setTextColor(android.graphics.Color.GRAY);
            count_layout.addView(page_text[i]);

/*            imgEventBanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                    // TODO Auto-generated method stub

                    Toast.makeText(getApplicationContext(), "galleryDot " + position, Toast.LENGTH_LONG).show();


                    for (int i = 0; i < count; i++) {
                        EventDetailActivity.page_text[i].setTextColor(android.graphics.Color.GRAY);
                    }
                    EventDetailActivity.page_text[position].setTextColor(android.graphics.Color.WHITE);

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });*/


            /*image banner item click*/

            imgEventBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventDetailActivity.this, BannerList.class);
                    intent.putExtra("eventObj", event);
                    startActivity(intent);
                }
            });

            callbackManager = CallbackManager.Factory.create();
            mShareBagroundResources.add(R.drawable.social_network_statlist);
            mShareBagroundResources.add(R.drawable.twitter_share_bg);
            mShareBagroundResources.add(R.drawable.whatsapp_selector);
            mShareAdapter = new ArrayAdapter<String>(this, R.layout.share_dialog_layout, R.id.share_dialog_text, mShareLIst) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Log.d(TAG, "getview called" + position);
                    View view = getLayoutInflater().inflate(R.layout.share_dialog_layout, parent, false);
                    ImageView img = (ImageView) view.findViewById(R.id.share_image_src);
                    TextView sharename = (TextView) view.findViewById(R.id.share_dialog_text);
                    sharename.setText(mShareLIst.get(position));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setBackground(getResources().getDrawable(mShareBagroundResources.get(position), getApplicationContext().getTheme()));
                        img.setImageDrawable(getResources().getDrawable(mShareResources.get(position), getApplicationContext().getTheme()));
                    } else {
                        img.setBackgroundDrawable(getResources().getDrawable(mShareBagroundResources.get(position)));
                        img.setImageDrawable(getResources().getDrawable(mShareResources.get(position)));
                    }
                    // ... Fill in other views ...
                    return view;
                }
            };

            mFacebookBtn = (ImageView) findViewById(R.id.facebook_btn);
            mFacebookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareWithfacebook();
                }
            });
            mTwitterBtn = (ImageView) findViewById(R.id.twitter_btn);
            mTwitterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postUsingTwitter();

                }
            });
            mWhatsAppBtn = (ImageView) findViewById(R.id.whatsapp_btn);
            mWhatsAppBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whatsapp(v);
                }
            });

        }

    }

    private void zoomImageFromThumb(final View thumbView, int zoomVal) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        final LinearLayout container_expandedImageView = (LinearLayout) findViewById(
                R.id.container_expanded_image);
        //  expandedImageView.setImageResource(imageResId);
        if (zoomVal == 1) {
            uImageLoader.displayImage(event.getEventLogo(), expandedImageView);
        } else if (zoomVal == 2) {
            uImageLoader.displayImage(event.getEventLogo_1(), expandedImageView);

        } else {
            uImageLoader.displayImage(event.getEventLogo_2(), expandedImageView);
        }
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.event_detail_container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        container_expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 4f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 4f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        container_expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    private void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().clear().commit();
        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent intent = new Intent(EventDetailActivity.this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void getCalender() {
        String DEBUG_TAG = "EventDetailActivity";
        String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.TITLE          // 2
        };

// The indices for the projection array above.
        int PROJECTION_ID_INDEX = 0;
        int PROJECTION_BEGIN_INDEX = 1;
        int PROJECTION_TITLE_INDEX = 2;

// Specify the date range you want to search for recurring
// event instances
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2011, 9, 23, 8, 0);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2011, 10, 24, 8, 0);
        long endMillis = endTime.getTimeInMillis();

        Cursor cur = null;
        ContentResolver cr = getContentResolver();

// The ID of the recurring event whose instances you are searching
// for in the Instances table
        String selection = CalendarContract.Instances.EVENT_ID + " = ?";
        String[] selectionArgs = new String[]{"207"};

// Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

// Submit the query
        cur = cr.query(builder.build(),
                INSTANCE_PROJECTION,
                selection,
                selectionArgs,
                null);
        try {
            while (cur.moveToNext()) {
                String title = null;
                long eventID = 0;
                long beginVal = 0;

                // Get the field values
                eventID = cur.getLong(PROJECTION_ID_INDEX);
                beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
                title = cur.getString(PROJECTION_TITLE_INDEX);

                // Do something with the values.
                Log.i(DEBUG_TAG, "Event:  " + title);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(beginVal);
                DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                Log.i(DEBUG_TAG, "Date: " + formatter.format(calendar.getTime()));
            }
            Log.d("test out", "Test manoj");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCalender() {
        Calendar beginTime = Calendar.getInstance();
        //  beginTime.set(2016, 5, 4, 7, 30);
        Date fullstartdate = null;
        Date fullenddate = null;
        Long sTime, eTime;
        try {
            Log.d("test date", event.getStartDate().toString());
            fullstartdate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(event.getStartDate());
            Log.d("test date", String.valueOf(fullstartdate));
            Log.d("test date", event.getEndDate().toString());
            fullenddate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(event.getEndDate().toString());
            Log.d("test date", String.valueOf(fullenddate));
            sTime = fullstartdate.getTime();
            eTime = fullenddate.getTime();
            beginTime.setTime(fullstartdate);
            Calendar endTime = Calendar.getInstance();
            //  endTime.set(2012, 0, 19, 8, 30);
            endTime.setTime(fullenddate);
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, sTime)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eTime)
                    .putExtra(CalendarContract.Events.TITLE, event.getEventName().toString())
                    .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription().toString())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getEventAddress())
                    .putExtra(ContactsContract.Intents.Insert.PHONE, event.getContact())
                    .putExtra(CalendarContract.ACTION_EVENT_REMINDER, true)
                    .putExtra(Intent.EXTRA_EMAIL, "manojmca15@gmail.com")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    .putExtra(Intent.EXTRA_EMAIL, event.getEventEmail());
            startActivity(intent);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public String getThumbnailImageUrl(String imgUrl, int width, int height) {
        String url = "http://imgsize.ph.126.net/?imgurl=data1_data2xdata3x0x85.jpg&enlarge=true";
        width = (int) (getResources().getDisplayMetrics().density * 100);
        height = (int) (getResources().getDisplayMetrics().density * 100); //just for convenient
        url = url.replaceAll("data1", imgUrl).replaceAll("data2", width + "").replaceAll("data3", height + "");
        return url;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //just for test to clean cache
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    private void sendShareStatustoServer() {
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        int eventId = Integer.parseInt(event.getId());
        int ruleid = 1;
        int ticketcount = 0;
        String activitydetail = "You have shared photo" + event.getEventName();
        serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL, eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                ruleid, Uri.encode(activitydetail), event.getEventLogo(), ticketcount), this);

    }

   /* private void sendShareStatustoServerUserActivity(int RuleId){
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        int eventId = Integer.parseInt(event.getId());
        int ruleid = 1;
        int ticketcount = 0;
        String activitydetail = "You have shared photo"+ event.getEventName();
        serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL,eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                ruleid,Uri.encode(activitydetail),event.getEventLogo(),ticketcount),this);

    }

    private void sendShareStatusUserActivity(int RuleId){

        long currentTime = System.currentTimeMillis();
        long lastsharedTime = PreferenceStorage.getEventSharedTime(this);
        int sharedCount = PreferenceStorage.getEventSharedcount(this);

        if( (currentTime - lastsharedTime)  > FindAFunConstants.TWENTY4HOURS ){
            Log.d(TAG,"event time elapsed more than 24hrs");
            PreferenceStorage.saveEventSharedtime(this, currentTime);
            PreferenceStorage.saveEventSharedcount(this, 1);

            //testing
            int ruleid = RuleId;
            int ticketcount = 0;
            String activitydetail = "You have shared photo"+ event.getEventName();
            int eventId = Integer.parseInt(event.getId());
            ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
            serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL,eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                    ruleid,Uri.encode(activitydetail),event.getEventLogo(),ticketcount),this);
            //testing


            sendShareStatustoServerUserActivity(RuleId);
        }else{
            if(sharedCount < 2){
                Log.d(TAG,"event shared cout is"+ sharedCount);
                sharedCount++;
                PreferenceStorage.saveEventSharedcount(this, sharedCount);
                sendShareStatustoServerUserActivity(RuleId);
            }
        }
    } */

    private void clickCountEvent() {
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        int eventId = Integer.parseInt(event.getId());
        serviceHelper.postShareDetails(String.format(FindAFunConstants.CLICK_COUNT_EVENT_URL, eventId), this);
    }

    private void sendShareStatustoServerUserActivity(int RuleId) {
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        int eventId = Integer.parseInt(event.getId());
        int ruleid = RuleId;
        int ticketcount = 0;
        String activitydetail = "You have shared photo" + event.getEventName();
        serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL, eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                ruleid, Uri.encode(activitydetail), event.getEventLogo(), ticketcount), this);

    }

    private void sendShareStatusUserActivity(int RuleId) {

        /*long currentTime = System.currentTimeMillis();
        long lastsharedTime = PreferenceStorage.getEventSharedTime(this);
        int sharedCount = PreferenceStorage.getEventSharedcount(this);

        if( (currentTime - lastsharedTime)  > FindAFunConstants.TWENTY4HOURS ){
            Log.d(TAG,"event time elapsed more than 24hrs");
            PreferenceStorage.saveEventSharedtime(this, currentTime);
            PreferenceStorage.saveEventSharedcount(this, 1);*/

        //testing
        int ruleid = RuleId;
        int ticketcount = 0;
        String statusCheckins = "";
        if(RuleId==1){
            statusCheckins = "You have shared photo ";
        }        if (RuleId==2) {
            statusCheckins = "You have checked in for the ";
        }        if (RuleId==3) {
            statusCheckins = "You have engaged for the ";
        } if (RuleId==3) {
            statusCheckins = "You have engaged for the ";
        }

        String activitydetail = "You have shared photo" + event.getEventName();
        int eventId = Integer.parseInt(event.getId());
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL, eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                ruleid, Uri.encode(activitydetail), event.getEventLogo(), ticketcount), this);
        //testing
        //  Toast.makeText(EventDetailActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();

        sendShareStatustoServerUserActivity(RuleId);
       /* }else{
            if(sharedCount < 2){
                Log.d(TAG,"event shared cout is"+ sharedCount);
                sharedCount++;
                PreferenceStorage.saveEventSharedcount(this, sharedCount);
                sendShareStatustoServerUserActivity(RuleId);
            }
        }*/
    }

    private void sendShareStatus() {

        //A user can only get points 3 times a day for photo sharing. So restrict beyond that
        long currentTime = System.currentTimeMillis();
        long lastsharedTime = PreferenceStorage.getEventSharedTime(this);
        int sharedCount = PreferenceStorage.getEventSharedcount(this);

        if ((currentTime - lastsharedTime) > FindAFunConstants.TWENTY4HOURS) {
            Log.d(TAG, "event time elapsed more than 24hrs");
            PreferenceStorage.saveEventSharedtime(this, currentTime);
            PreferenceStorage.saveEventSharedcount(this, 1);

            //testing
            int ruleid = 1;
            int ticketcount = 0;
            String activitydetail = "You have shared photo" + event.getEventName();
            int eventId = Integer.parseInt(event.getId());
            ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
            serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL, eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                    ruleid, Uri.encode(activitydetail), event.getEventLogo(), ticketcount), this);
            //testing

            sendShareStatustoServer();
        } else {
            if (sharedCount < 3) {
                Log.d(TAG, "event shared cout is" + sharedCount);
                sharedCount++;
                PreferenceStorage.saveEventSharedcount(this, sharedCount);
                sendShareStatustoServer();
            }
        }

    }

    private void shareWithfacebook() {
        shareDialog = new ShareDialog(EventDetailActivity.this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG, "Share success");
                Toast.makeText(EventDetailActivity.this, "Shared event using facebook", Toast.LENGTH_SHORT).show();
                sendShareStatus();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Share cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Share failed" + error.getLocalizedMessage());
                Toast.makeText(EventDetailActivity.this, "Error posting message. Please try again", Toast.LENGTH_SHORT);

            }
        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            Log.d(TAG, "Share can be done");
            String title = event.getEventName();
            String description = event.getDescription();
            Uri uri = Uri.parse("android.resource://http://www.heylaapp.com/wp-content/uploads/2015/12/logo-ho.png");
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .setContentUrl(Uri.parse("http://www.heylaapp.com/"))
                    .setImageUrl(Uri.parse("http://www.heylaapp.com/wp-content/uploads/2015/12/logo-ho.png"))
                    .build();

            shareDialog.show(linkContent);
        } else {
            Log.d(TAG, "cant share content using facebook");
        }

    }

    private boolean twitterLoggedIn() {
        boolean loggedin = PreferenceStorage.getTwitterLoggedIn(this);
        return loggedin;
    }

    private void postUsingTwitter() {
        if (!CommonUtils.isNetworkAvailable(getApplicationContext())) {
            Log.d(TAG, "No Network connection");
            Toast.makeText(this, "No Networkconnection", Toast.LENGTH_SHORT).show();
            return;

        }

        if ((FindAFunConstants.TWITTER_CONSUMER_KEY.trim().length() == 0) || (FindAFunConstants.TWITTER_CONSUMER_SECRET.trim().length() == 0)) {
            Log.d(TAG, "Consumer key or secret key is bot set");
            Toast.makeText(this, "Please set Twitter Consumer key and Consumer secret", Toast.LENGTH_SHORT).show();
            return;
        }

        if (twitterLoggedIn()) {
            Log.d(TAG, "Already Logged In to twitter");
            String text = event.getEventName();

            //  shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, "www.Hobbistan.com");
            // shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            String message = "http://www.Hobbistan.com " + event.getEventName() + "\n" + event.getDescription();
            String shortText = "";
            if (message.length() >= 140) {
                Log.d(TAG, "length greater than 140" + message.length());
                shortText = message.substring(0, 139);
                message = shortText;
            }
            Log.d(TAG, "length greater than 140" + message.length());
            if ((message != null) && !message.isEmpty()) {

                new TwitterUpdateStatusTask().execute(message);
            }
           /* Intent intent = new Intent(this, TwitterActivity.class);
            startActivity(intent);*/

        } else {
            Log.d(TAG, "Start twitter oAuth");
            //Set the current event
            GamificationDataHolder.getInstance().setmCurrentEvent(event);
            new TwitterAuthenticateTask(this).execute();
        }

    }

    @Override
    public void onSuccess(int resultCode, Object result) {
        Log.d(TAG, "Succesfully posted share status");

    }

    @Override
    public void onError(String erorr) {
        Log.d(TAG, "error while sending status update" + erorr);

    }

    @Override
    public void onEventResponse(JSONObject response) {
        Toast.makeText(this, "Event boookmarked succesfully", Toast.LENGTH_SHORT).show();
        GamificationDataHolder.getInstance().addBookmarkedEvent(event.getId());
        setCalender();
    }

    @Override
    public void onEventError(String error) {
        Log.e(TAG, "Error while bookmarking event");
        Toast.makeText(this, "Error bookmarking event", Toast.LENGTH_SHORT).show();

    }

    class TwitterUpdateStatusTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getApplicationContext(), "Event shared successfully using twitter", Toast.LENGTH_SHORT).show();
                sendShareStatus();
                // finish();
            } else
                Toast.makeText(getApplicationContext(), "Tweet failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");

                if (((accessTokenString != null) && (accessTokenString.length() > 0))
                        && ((accessTokenSecret != null) && (accessTokenSecret.length() > 0))) {
                    AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                    twitter4j.Status status = TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken).updateStatus(params[0]);
                    return true;
                }

            } catch (TwitterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.

        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Received FacebookActivity share result...");
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //LinkedIN call
        // mLinkedinSessionManager.onActivityResult(this,requestCode,resultCode,data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_directions) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + event.getEventLatitude() + "," + event.getEventLongitude()));
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            Log.d(TAG, "home up button selected");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("Google connection", "" + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private void getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
        } else {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void checkPermissions() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    isPermissionGranted = true;
                    getLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class SwipeGestureDetector implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("swipe", "onDown: ");

            Intent intent = new Intent(getApplicationContext(), imageGallery.class);
            intent.putStringArrayListExtra("image_list", imgList);
            //startActivity(intent);
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d("swipe", "onShowPress: ");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {


                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imgEventBanner.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));
                    imgEventBanner.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));
                    // controlling animation

                    imgEventBanner.getInAnimation().setAnimationListener(mAnimationListener);
                    imgEventBanner.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imgEventBanner.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));
                    imgEventBanner.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));
                    // controlling animation
                    imgEventBanner.getInAnimation().setAnimationListener(mAnimationListener);
                    imgEventBanner.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

}
