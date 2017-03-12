package com.findafun.activity;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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
import com.findafun.adapter.BannerAdapter;
import com.findafun.app.AppController;
import com.findafun.bean.events.Event;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.helper.AlertDialogHelper;
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

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

/**
 * Created by Data Crawl 6 on 14-05-2016.
 */
public class StaticEventDetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, IGamificationServiceListener, IEventServiceListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private Animation.AnimationListener mAnimationListener;

    private Animator mCurrentAnimator;
    private static final String TAG = StaticEventDetailActivity.class.getName();
    private Event event;
    private TextView txtEventName, txtEventCategory, txtEventDesc, txtEventVenue, txtEventStartDate, txtEventEndDate, txtEventStartTime, txtEventEndTime;
    private TextView txtEventTime, txtEventDate, txtEventEntry, txtEventContact, txtEventEmail, txtWebSite;
    private TextView txtViewMore, txtViewLess;
    private ViewFlipper imgEventBanner;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
    LinearLayout count_layout;
    int count = 0;
    static TextView page_text[];

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
    ImageView banner_image_one, banner_image_two, banner_image_three, banner_image_four, banner_image_five;
    Button whishListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.activity_static_event_detail_new);

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

       /*   new AppRate(this)
                    .setMinDaysUntilPrompt(2)
                    .setMinLaunchesUntilPrompt(3)
                    .init();*/

        initializeViews();
        event = (Event) getIntent().getSerializableExtra("eventObj");
        populateData();

        if (curRate == 2) {
            fetchAppRate();
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

                shareIntent.putExtra(Intent.EXTRA_TITLE, "www.Hobbistan.com");
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
                    Intent intent = new Intent(StaticEventDetailActivity.this, MapsActivity.class);
                    intent.putExtra("eventObj", event);
                    startActivity(intent);
                } else {
                    Toast.makeText(StaticEventDetailActivity.this, "Location information not available for this event", Toast.LENGTH_SHORT).show();
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


        //  "http://placehold.it/120x120&text=image2"

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


        imgList.add(0, event.getEventLogo());
        imgList.add(1, "http://placehold.it/120x120&text=image2");
        imgList.add(2, "http://placehold.it/120x120&text=image3");

        try {



       /* if(!event.getEventLogo_1().isEmpty()){
            uImageLoader.displayImage(event.getEventLogo_1(), banner_image_two);
            imgList.add(1,event.getEventLogo_1());
            imgEventBanner.startFlipping();

            if(!event.getEventLogo_2().isEmpty()){
                uImageLoader.displayImage(event.getEventLogo_2(), banner_image_three);
                imgList.add(2,event.getEventLogo_2());

            }



        }*/


        } catch (Exception e) {
            e.printStackTrace();
        }


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
        builderSingle.setTitle("Share the world using");
        // builderSingle.setTitle("Share Event Using");
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
        String activitydetail = "You have shared photo" + event.getEventName();
        int eventId = Integer.parseInt(event.getId());
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL, eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                ruleid, Uri.encode(activitydetail), event.getEventLogo(), ticketcount), this);
        //testing
        //  Toast.makeText(StaticEventDetailActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();

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


    private void initializeViews() {

        banner_image_one = (ImageView) findViewById(R.id.banner_image_one);
        // banner_image_one.set
        banner_image_two = (ImageView) findViewById(R.id.banner_image_two);
        banner_image_three = (ImageView) findViewById(R.id.banner_image_three);

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

        whishListBtn = (Button) findViewById(R.id.whishlist1_btn);
        LinearLayout shareBtn = (LinearLayout) findViewById(R.id.share_btn);
        LinearLayout contactBtn = (LinearLayout) findViewById(R.id.contact_btn);

        LinearLayout engageBtn = (LinearLayout) findViewById(R.id.engage_btn);
        Button bookingBtn = (Button) findViewById(R.id.booking_btn);
        Switch checkinsBtn = (Switch) findViewById(R.id.checkins_btn);

        whishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {
                Log.d(TAG, "Bookmark Button selected" + event.getId());
                if (mServiceHelper == null) {
                    mServiceHelper = new EventServiceHelper(StaticEventDetailActivity.this);
                    mServiceHelper.setEventServiceListener(StaticEventDetailActivity.this);

                }
                if (GamificationDataHolder.getInstance().isEventBookmarked(event.getId())) {
                    Toast.makeText(StaticEventDetailActivity.this, "Event already bookmarked", Toast.LENGTH_SHORT).show();
                    int imgResource = R.drawable.ic_wishlist1_selected;
                    whishListBtn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);

                } else {
                    try {
                        mServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.ADD_EVENT_BOOKMARK,
                                Integer.parseInt(PreferenceStorage.getUserId(StaticEventDetailActivity.this)), Integer.parseInt((event.getId()))));
                        int imgResource = R.drawable.ic_wishlist1_selected;
                        whishListBtn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(StaticEventDetailActivity.this);
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
        };
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {

                    showShareList();
                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(StaticEventDetailActivity.this);
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
                        StaticEventDetailActivity.this);
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






               /* builder.setMessage("Engage Your Event");

                builder.setPositiveButton("Interested",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(getApplicationContext(),"Interested is clicked",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("Going on",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(getApplicationContext(),"Going on is clicked",Toast.LENGTH_LONG).show();
                            }
                        });
*/
                builder.show();


                sendShareStatusUserActivity(3);

                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(StaticEventDetailActivity.this);
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


                Toast.makeText(getApplicationContext(), "Booking is currently not available for the event - " + event.getEventName().toString() + "\nPlease try later.", Toast.LENGTH_LONG).show();

                // sendShareStatusUserActivity(4);

            }
        });


        checkinsBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {


                    if (isChecked) {
                    Toast.makeText(getApplicationContext(), "You have successfully checked-in for the event - " + event.getEventName().toString() + "\nGet ready for the fun! ", Toast.LENGTH_LONG).show();
                    sendShareStatusUserActivity(2);
                } else {

                }
                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(StaticEventDetailActivity.this);
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


/*
        checkinsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getApplicationContext(),"You have successfully checkin for the event "+event.getEventName().toString()+"\n Enjoy!",Toast.LENGTH_LONG).show();

                sendShareStatusUserActivity(2);

            }
        });
*/

        contactBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String phoneNumber = event.getContact();


                if ((phoneNumber != null) && !(phoneNumber.isEmpty())) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            StaticEventDetailActivity.this);
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


        //  imgList.add(0, event.getEventLogo());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(StaticEventDetailActivity.this));

        BannerAdapter adapter = new BannerAdapter(this, imgList);

        //  imgEventBanner.setAdapter(adapter);

      /*  imgEventBanner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                  new BannerAdapter(getApplicationContext(), imgList);


                detector.onTouchEvent(event);
                return true;
            }
        });
*/


        count = 3;// imgEventBanner.getAdapter().getCount();
        page_text = new TextView[count];
        for (int i = 0; i < count; i++) {
            page_text[i] = new TextView(this);
            page_text[i].setText(".");
            page_text[i].setTextSize(45);
            page_text[i].setTypeface(null, Typeface.BOLD);
            page_text[i].setTextColor(android.graphics.Color.GRAY);
            count_layout.addView(page_text[i]);

       /*     imgEventBanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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


            imgEventBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StaticEventDetailActivity.this, BannerList.class);
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

    private void sendShareStatus() {

        //A user can only get points 3 times a day for photo sharing. So restrict beyond that
        long currentTime = System.currentTimeMillis();
        long lastsharedTime = PreferenceStorage.getEventSharedTime(this);
        int sharedCount = PreferenceStorage.getEventSharedcount(this);

        if ((currentTime - lastsharedTime) > FindAFunConstants.TWENTY4HOURS) {
            Log.d(TAG, "event time elapsed more than 24hrs");
            PreferenceStorage.saveEventSharedtime(this, currentTime);
            PreferenceStorage.saveEventSharedcount(this, 1);
            sendShareStatustoServer();
        } else {
            if (sharedCount < 3) {
                Log.d(TAG, "event shared cout is" + sharedCount);
                sharedCount++;
                PreferenceStorage.saveEventSharedcount(this, sharedCount);
                sendShareStatustoServer();
            } //+971561585606
        }
    }

    private void shareWithfacebook() {
        shareDialog = new ShareDialog(StaticEventDetailActivity.this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG, "Share success");
                Toast.makeText(StaticEventDetailActivity.this, "Shared event using facebook", Toast.LENGTH_SHORT).show();
                sendShareStatus();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Share cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Share failed" + error.getLocalizedMessage());
                Toast.makeText(StaticEventDetailActivity.this, "Error posting message. Please try again", Toast.LENGTH_SHORT);
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
            startActivity(intent);
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
    private void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().clear().commit();
        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent intent = new Intent(StaticEventDetailActivity.this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
