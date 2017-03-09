package com.findafun.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.adapter.NavDrawerAdapter;
import com.findafun.bean.events.Event;
import com.findafun.bean.events.EventList;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.customview.PagerSlidingTabStrip;
import com.findafun.fragment.LandingPagerFragment;
import com.findafun.fragment.StaticEventFragment;
import com.findafun.fragment.StaticFragment;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.pageradapter.LandingPagerAdapter;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.servicehelpers.ShareServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.twitter.TwitterUtil;
import com.findafun.utils.AndroidMultiPartEntity;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class LandingActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, LandingPagerAdapter.onFragmentsRegisteredListener, DialogClickListener, IEventServiceListener, IGamificationServiceListener {

    private static final String TAG = LandingActivity.class.getName();
    private static final int TAG_LOGOUT = 100;
    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ViewPager viewPager;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private LandingPagerAdapter landingPagerAdapter;
    private ListView navDrawerList;
    boolean doubleBackToExitPressedOnce = false;
    int checkPointSearch = 0;
    private SharedPreferences TransPrefs;
    private boolean isFirstRunAdvsearch = true;
    private ImageView imgNavHeaderBg, imgNavProfileImage;
    public static final int TAG_FAVOURITES = 0, TAG_FEATURED = 1, TAG_ALL = 2;
    private ArrayAdapter<String> navListAdapter;
    private String[] values = {"Change City", "Profile", "Edit Preferences", "Wishlists", "Refer & Earn", "Rate Us", "Sign Out"};
    private boolean mFragmentsLoaded = false;
    TextView navUserName = null;
    TextView navUserCity = null;
    private SearchView mSearchView = null;
    private String mCurrentUserProfileUrl = "";
    private Bitmap mBitmapToLoad = null;
    private String mUpdatedImageUrl = null;
    protected EventServiceHelper eventServiceHelper;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
        // getSupportActionBar().setLogo(R.drawable.ic_navigation_drawer);
        //initialize navigation drawer
        initializeNavigationDrawer();
        initializeViews();
        context = getApplicationContext();
        eventServiceHelper = new EventServiceHelper(this);
        eventServiceHelper.setEventServiceListener(this);
        fetchBookmarks();
        sendShareStatustoServer();
    }

    private void sendShareStatustoServer() {
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        int eventId = 0;
        int ruleid = 5;
        int ticketcount = 0;
        String activitydetail = "Login Status";
        serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_LOGIN_COUNT_URL, eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                ruleid, Uri.encode(activitydetail), ticketcount), this);
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    private void fetchBookmarks() {
        if (CommonUtils.isNetworkAvailable(this)) {
            eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_BOOKMARK, Integer.parseInt(PreferenceStorage.getUserId(this))));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("LandingonPause", "LandingonPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LandingonResume", "LandingonResume");
        if (PreferenceStorage.getFilterApply(this)) {
            PreferenceStorage.IsFilterApply(this, false);
            String singledate = PreferenceStorage.getFilterSingleDate(this);

            LandingPagerFragment landingPagerFragment = (LandingPagerFragment)
                    landingPagerAdapter.getRegisteredFragment(getTaskId());
            if (landingPagerFragment != null) {
                checkPointSearch = 1;
                landingPagerFragment.callGetFilterService();
            }

            LandingPagerFragment landingPagerFragment1 = (LandingPagerFragment)
                    landingPagerAdapter.getRegisteredFragment(getTaskId());
            if (landingPagerFragment1 != null) {
                checkPointSearch = 1;
                landingPagerFragment1.callGetFilterService();
            }

            StaticEventFragment staticEventFragment = (StaticEventFragment)
                    landingPagerAdapter.getRegisteredFragment(getTaskId());
            if (staticEventFragment != null) {
                checkPointSearch = 2;
                staticEventFragment.callGetFilterService();
            }

            //Toast.makeText(this,"filter service called",Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        Log.d(TAG, "initializin the views");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPagerSlidingTabStrip.setShouldExpand(true);
        landingPagerAdapter = new LandingPagerAdapter(this, getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(landingPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        Log.d(TAG, "initializing view pager");
        mPagerSlidingTabStrip.setViewPager(viewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(this);
        navUserName = (TextView) findViewById(R.id.user_profile_name);
        navUserCity = (TextView) findViewById(R.id.txt_city_name);
        String userName = PreferenceStorage.getUserName(getApplicationContext());
        String userCity = PreferenceStorage.getUserCity(getApplicationContext());
        Log.d(TAG, "user name value" + userName);
        if ((userName != null) && !userName.isEmpty()) {
            navUserName.setText(userName);
        }
        if ((userCity != null) && !userCity.isEmpty()) {
            navUserCity.setText(userCity);
        }
        //fetch Facebook profile picture
        int loginMode = PreferenceStorage.getLoginMode(this);
        String url = PreferenceStorage.getProfileUrl(this);
        if ((url == null) || (url.isEmpty())) {
            if ((loginMode == 1) || (loginMode == 3)) {
                url = PreferenceStorage.getSocialNetworkProfileUrl(this);
            }
        }
        mCurrentUserProfileUrl = url;
        Log.d(TAG, "Login Mode is" + loginMode);
        /*if((loginMode == FindAFunConstants.FACEBOOK) || (loginMode == FindAFunConstants.GOOGLE_PLUS)){

            Log.d(TAG,"fetching the image url"+ url);

        }*/
        if (((url != null) && !(url.isEmpty()))) {
            Log.d(TAG, "image url is " + url);
            Picasso.with(this).load(url).placeholder(R.drawable.ab_hobbistan_logo).error(R.drawable.ab_hobbistan_logo).into(imgNavProfileImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Image uploaded successfully using picasso");
                            try {
                                if (shouldUploadSocialNetworkPic()) {
                                    mUpdatedImageUrl = null;
                                    mBitmapToLoad = ((BitmapDrawable) imgNavProfileImage.getDrawable()).getBitmap();
                                    new UploadFileToServer().execute();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        Log.d(TAG, "Set the selected page to 0");//default page
        // makeGetEventListServiceCall(0);
    }

    private boolean shouldUploadSocialNetworkPic() {
        boolean upload = false;
        String url = PreferenceStorage.getSocialNetworkProfileUrl(LandingActivity.this);
        String userimageUrl = PreferenceStorage.getProfileUrl(LandingActivity.this);
        int loginMode = PreferenceStorage.getLoginMode(LandingActivity.this);
        if ((userimageUrl == null) || (userimageUrl.isEmpty())) {
            if ((loginMode == 1) || (loginMode == 3)) {
                if ((url != null) && !(url.isEmpty())) {
                    Bitmap imageBitmap = ((BitmapDrawable) imgNavProfileImage.getDrawable()).getBitmap();
                    Log.d(TAG, "valid URL present");
                    if (imageBitmap != null) {
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


    private void initializeNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {
                String userName = PreferenceStorage.getUserName(getApplicationContext());
                String userCity = PreferenceStorage.getUserCity(getApplicationContext());
                String url = PreferenceStorage.getProfileUrl(LandingActivity.this);
                int loginMode = PreferenceStorage.getLoginMode(LandingActivity.this);
                if ((url == null) || (url.isEmpty())) {
                    if ((loginMode == 1) || (loginMode == 3)) {
                        url = PreferenceStorage.getSocialNetworkProfileUrl(LandingActivity.this);
                    }

                }
                Log.d(TAG, "user name value" + userName);
                if ((userName != null) && !userName.isEmpty()) {
                    String[] splitStr = userName.split("\\s+");
                    navUserName.setText(splitStr[0]);

                }

                if ((userCity != null) && !userCity.isEmpty()) {
                    navUserCity.setText(userCity);
                }
                if (((url != null) && !(url.isEmpty())) && !(url.equalsIgnoreCase(mCurrentUserProfileUrl))) {
                    Log.d(TAG, "image url is " + url);
                    mCurrentUserProfileUrl = url;
                    Picasso.with(LandingActivity.this).load(url).noPlaceholder().error(R.drawable.ab_hobbistan_logo).into(imgNavProfileImage);
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // enable ActionBar app icon to behave as action to toggle nav drawer
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        // Initialize header and listview
        navDrawerList = (ListView) findViewById(R.id.nav_drawer_options_list);
        NavDrawerAdapter navDrawerAdapter = new NavDrawerAdapter(getApplicationContext(), R.layout.nav_list_item, values);
        navListAdapter = new ArrayAdapter<String>(this, R.layout.nav_list_item, values);
        navDrawerList.setAdapter(navDrawerAdapter);
        // imgNavHeaderBg = (ImageView) findViewById(R.id.img_nav_header_background);
        imgNavProfileImage = (ImageView) findViewById(R.id.img_profile_image);
        navDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onNavigationMenuSelected(position);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        //populateNavDrawerHeaderView();
    }

    private void onNavigationMenuSelected(int position) {
        if (position == 1) {

            if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {
                Intent navigationIntent = new Intent(this, ProfileActivity.class);
                navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(navigationIntent);
            } else {

                AlertDialogHelper.showCompoundAlertDialog(this, "Login", "Login to access", "OK", "CANCEL", 1);
            }

        } else if (position == 0) {
            Intent navigationIntent = new Intent(this, SelectCityActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 2) {
            Intent navigationIntent = new Intent(this, SelectPreferenceActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 3) {
            if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {
                Intent navigationIntent = new Intent(this, BookmarksActivity.class);
                //navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(navigationIntent);
            } else {

                AlertDialogHelper.showCompoundAlertDialog(this, "Login", "Login to access", "OK", "CANCEL", 1);
            }
        } else if (position == 4) {
            if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
                i.putExtra(android.content.Intent.EXTRA_TEXT, "Hey! Get the Hobbistan app and win some exciting rewards. Use my promo code " + PreferenceStorage.getPromoCode(LandingActivity.this) + " http://heylaapp.com/app/referral/click.php?id=" + PreferenceStorage.getUserId(LandingActivity.this) + "");
                startActivity(Intent.createChooser(i, "Share via"));
            } else {

                AlertDialogHelper.showCompoundAlertDialog(this, "Login", "Login to access", "OK", "CANCEL", 1);
            }
        } else if (position == 5) {
            Log.d(TAG, "Rate Us");
            rateUs();
        } else if (position == 6) {
            Log.d(TAG, "Perform Logout");
            doLogout();
        }
    }

    private void rateUs() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.palprotech.heyla&hl=en");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void populateNavDrawerHeaderView() {
        Picasso.with(this)
                .load("http://www.hdwallpapersnew.net/wp-content/uploads/2015/04/awesome-scenery-widescreen-high-resolution-for-desktop-background-wallpaper-pictures-free.jpg")
                .fit()
                .placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                .into(imgNavHeaderBg);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_landi/*ng, menu);
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_landing, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) menu.findItem(R.id.action_search_view).getActionView();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Search button clicked");
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                Log.d(TAG, "Query submitted with String:" + s);
                int currentpage = viewPager.getCurrentItem();
                Log.d(TAG, "current item is" + currentpage);
                if (checkPointSearch == 2) {
                    StaticFragment staticFragment = (StaticFragment)
                            landingPagerAdapter.getRegisteredFragment(currentpage);
                    if (staticFragment != null) {
                        staticFragment.searchForEvent(s);
                    }
                } else if (checkPointSearch == 1) {
                    LandingPagerFragment landingPagerFragment = (LandingPagerFragment)
                            landingPagerAdapter.getRegisteredFragment(currentpage);
                    if (landingPagerFragment != null) {
                        landingPagerFragment.searchForEvent(s);
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                int currentpage = viewPager.getCurrentItem();
                Log.d(TAG, "current item is" + currentpage);
                LandingPagerFragment landingPagerFragment = (LandingPagerFragment)
                        landingPagerAdapter.getRegisteredFragment(currentpage);

                if ((s != null) && (!s.isEmpty())) {
                    if (landingPagerFragment != null) {
                        landingPagerFragment.searchForEvent(s);
                    }
                } else {
                    if (landingPagerFragment != null) {
                        Log.d(TAG, "call exit search");
                        landingPagerFragment.exitSearch();
                    }
                }

                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                Log.d(TAG, "searchView closed");

                int currentpage = viewPager.getCurrentItem();
                Log.d(TAG, "current item is" + currentpage);
                LandingPagerFragment landingPagerFragment = (LandingPagerFragment)
                        landingPagerAdapter.getRegisteredFragment(currentpage);
                if (landingPagerFragment != null) {
                    Log.d(TAG, "call exit search");
                    landingPagerFragment.exitSearch();
                }

                return false;
            }
        });

        mSearchView.setQueryHint("Search Event name");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Workaround for SearchView close listener
        switch (item.getItemId()) {
            case R.id.action_filter:
                //ajaz
                // Toast.makeText(this, "advance filter clicked", Toast.LENGTH_SHORT).show();
                Context appContext = this;

          /*      final Dialog dialog = new Dialog(appContext,android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.transparent_favourite);
                TransPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
                isFirstRunAdvsearch = TransPrefs.getBoolean("isFirstRunAdvsearch", true);

                if (isFirstRunAdvsearch) {
                    dialog.show();
                    final TextView txtAdvsearch = (TextView) dialog.findViewById(R.id.trans_search);

                    txtAdvsearch.setVisibility(View.VISIBLE);
                    txtAdvsearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    isFirstRunAdvsearch=false;
                    TransPrefs.edit().putBoolean("isFirstRunAdvsearch", isFirstRunAdvsearch).commit();

                } else {
                    dialog.dismiss();
                    startActivity(new Intent(LandingActivity.this, AdvanceSearchAct.class));
                    return true;

                } */

                startActivity(new Intent(LandingActivity.this, AdvanceSearchAct.class));

            default:
                return super.onOptionsItemSelected(item);
        }

        // return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "page selected" + position);
        if (landingPagerAdapter != null) {
            mPagerSlidingTabStrip.notifyDataSetChanged();

            getSupportActionBar().setTitle(landingPagerAdapter.getPageTitle(position));
            if (position == 3) { //Rewards Fragment. So search bar is not needed
                if (mSearchView != null) {
                    mSearchView.setVisibility(View.GONE);
                }

            } else {
                if (mSearchView != null) {
                    mSearchView.setVisibility(View.VISIBLE);
                }
                LandingPagerFragment landingPagerFragment = (LandingPagerFragment)
                        landingPagerAdapter.getRegisteredFragment(position);
                if (landingPagerFragment != null) {
                    landingPagerFragment.exitSearch();
                }
            }
        }
        makeGetEventListServiceCall(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void makeGetEventListServiceCall(int position) {
        Log.d(TAG, "Fetching the event list for pos" + position);
        LandingPagerFragment landingPagerFragment = (LandingPagerFragment)
                landingPagerAdapter.getRegisteredFragment(position);
        if (landingPagerFragment != null) {
            landingPagerFragment.callGetEventService(position);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        if (focus) {
            LandingPagerFragment landingPagerFragment = (LandingPagerFragment)
                    landingPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
            if (landingPagerFragment != null) {
                landingPagerFragment.onWindowFocusChanged();
            }
        }
    }

    @Override
    public void onFragmentsRegistered() {
        Log.d(TAG, "Fragment registered called");
        // makeGetEventListServiceCall(TAG_FAVOURITES);
        if (!mFragmentsLoaded) {
            Log.d(TAG, "fetch for the first time");
            makeGetEventListServiceCall(TAG_FAVOURITES);
            mFragmentsLoaded = true;
        }
    }

    public void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().commit();
        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent homeIntent = new Intent(this, SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        this.finish();
    }

    @Override
    public void onAlertPositiveClicked(int tag) {
        if (tag == TAG_LOGOUT) {
            doLogout();

        }
    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onEventResponse(JSONObject response) {
        Gson gson = new Gson();
        EventList eventsList = gson.fromJson(response.toString(), EventList.class);
        if (eventsList.getEvents() != null && eventsList.getEvents().size() > 0) {
            // totalCount = eventsList.getCount();
            GamificationDataHolder.getInstance().clearBookmarks();
            for (Event event : eventsList.getEvents()) {
                GamificationDataHolder.getInstance().addBookmarkedEvent(event.getId());
            }
        }
    }

    @Override
    public void onEventError(String error) {
        GamificationDataHolder.getInstance().clearBookmarks();
    }

    @Override
    public void onSuccess(int resultCode, Object result) {

    }

    @Override
    public void onError(String erorr) {

    }

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
            httppost = new HttpPost(String.format(FindAFunConstants.UPLOAD_PROFILE_IMAGE, Integer.parseInt(PreferenceStorage.getUserId(LandingActivity.this))));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                //  publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                Log.d(TAG, "upload image");
                if (mBitmapToLoad != null) {

                    String fileNameVal = PreferenceStorage.getUserId(LandingActivity.this) + ".png";
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    mBitmapToLoad.compress(Bitmap.CompressFormat.PNG, 75, bos);
                    byte[] data = bos.toByteArray();
                    ByteArrayBody bab = new ByteArrayBody(data, fileNameVal);
                    entity.addPart("fileToUpload", bab);

                    //entity.addPart("image", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(LandingActivity.this)));
                /*entity.addPart("website",
                        new StringBody("www.ahmed.site40.net"));
                entity.addPart("email", new StringBody("ahmedchoteri@gmail.com"));*/

                    // totalSize = entity.getContentLength();
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

            if (mUpdatedImageUrl != null) {
                PreferenceStorage.saveProfilePic(LandingActivity.this, mUpdatedImageUrl);
            }
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
}
