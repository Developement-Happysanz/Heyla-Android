package com.findafun.utils;

/**
 * Created by zahid.r on 10/30/2015.
 */
public class FindAFunConstants {

    // URLS
//    public static final String BASE_URL = "http://heylaapp.com/app/";
    public static final String BASE_URL = "http://heylaapp.com/old_site/app/";
    public static final String BASE_URL_1 = "http://heylaapp.com/old_site/";
    public static final String GET_SIGN_UP_URL = "http://heylaapp.com/old_site/app/api.php";
    //http://hobbistan.com/app/hobbistan/api.php?func_name=event_popular&event_id=530
    public static final String SET_CATEGORY_URL = BASE_URL + "api.php";
    public static final String GET_ADVANCE_SINGLE_SEARCH = BASE_URL + "api.php";
    public static final String GET_EVENTS_FAVOURITES_1 = BASE_URL_1 + "api.php?func_name=event_management&event_type=bars&city=chennai";
    public static final String GET_EVENTS_FAVOURITES = BASE_URL + "api.php?func_name=event_management&event_type=preference&user_id=%d&page_id=%d&city=%s&user_type=%s";
    public static final String GUEST_LOGIN = BASE_URL + "api.php?func_name=skip_sign_up&imei_number=%s&gcm_key=%s&mobile_type=%s";
    public static final String GET_EVENTS_BOOKING_PLAN = BASE_URL + "api.php?func_name=booking_plans&event_id=%s";
    public static final String GET_ADVANCE_SEARCH = BASE_URL + "api.php?func_name=advanced_event_management&from_date04-03-2015=preference&user_id=%d&page_id=%d&city=%s";
    public static final String GET_EVENTS_FEATURED = BASE_URL + "api.php?func_name=event_management&event_type=popularity&page_id=%d&city=%s";
    public static final String GET_EVENTS_NEARBY_URL = BASE_URL + "api.php?func_name=event_management&event_type=all&page_id=%d";
    public static final String GET_EVENTS_ALL_URL = BASE_URL + "api.php?func_name=event_management&event_type=all&page_id=%d&city=%s";
    public static final String GET_EVENTS_ALL_TEST_URL = BASE_URL + "api.php?func_name=event_management&event_type=all&user_id=%d&page_id=%d";
    public static final String GET_STATIC_EVENTS = BASE_URL + "api.php?func_name=event_management_static&event_type=static_event&user_id=%d&page_id=%d&city=%s";
    public static final String GET_CATEGORY_URL = BASE_URL + "api.php?";
    public static final String GET_CATEGORY_LIST_URL = BASE_URL + "api.php?func_name=getCategoryList";
    public static final String GET_CITY_URL = BASE_URL + "api.php?func_name=getCity";
    public static final String GET_CITY_NEW_URL = BASE_URL + "api.php?func_name=getCitynew&country=%s";
    public static final String GET_COUNTRY_URL = BASE_URL + "api.php?func_name=getCountry";
    public static final String UPDATE_PROFILE_URL = "http://heylaapp.com/old_site/app/api.php?func_name=updateProfile&user_email=%s&user_name=%s&user_id=%d&user_city=%s&user_gender=%s&user_birthday=%s&user_occupation=%s&user_phone=%s";
    //    public static final String UPDATE_PROFILE_URL = "http://heylaapp.com/old_site/app/api.php?func_name=updateProfile&user_email=%s&user_name=%s&user_id=%d&user_password=%s&user_city=%s&user_gender=%s&user_birthday=%s&user_occupation=%s&user_phone=%s";
    public static final String ADD_EVENT_URL = "http://heylaapp.com/old_site/app/api.php?func_name=user_add_event&event_type=%d&txtEvent=%s&cboCategory=%s&cboCity=%s&txtVenue=%s&txtAddress=%s&txtDescription=%s&cboCost=%s&txtSdate=%s&txtEdate=%s&cboStime=%s&cboEtime=%s&txtLatitude=%s&txtLongitude=%s&txtPhone=%s&txtPerson=%s&txtEmail=%s&txtpic1=%s&txtpic2=%s&txtpic3=%s";
    public static final String UPDATE_CITY = "http://heylaapp.com/old_site/app/api.php?func_name=update_usercity&user_id=%d&city=%s&country=%s&user_type=%d";
    public static final String UPLOAD_PROFILE_IMAGE = BASE_URL + "upload.php?user_id=%d";
    public static final String GET_EVENTS_BOOKMARK = BASE_URL + "api.php?func_name=fetch_event_bookmark&user_id=%d";
    public static final String ADD_EVENT_BOOKMARK = BASE_URL + "api.php?func_name=add_event_bookmark&user_id=%d&event_id=%d";
    public static final String SAVE_REVIEW = BASE_URL + "api.php?func_name=add_review&user_id=%d&event_id=%d&comments=%s&ratings=%d";
    public static final String NEARBY_DISTANCE_RADIATION = BASE_URL + "api.php?func_name=nearby_gmap&event_latitude=%s&event_longitude=%s&distance=%s&event_type=%s";
    public static final String BOOKING_TICKET = BASE_URL + "api.php?func_name=booking_add&order_id=%s&event_id=%s&plan_id=%s&user_id=%s&no_seats=%s&booking_date=%s&amount=%s";

    public static final String GET_REWARDS = BASE_URL + "fetchActivity.php?getUserPointsDetail=true&id=%d";
    public static final String GET_LEADER_BOARD = BASE_URL + "fetchActivity.php?getLeaderBoard=true";
    public static final String GET_LOCAL_LEADER_BOARD = BASE_URL + "fetchActivity.php?getLeaderBoardcity=true&id=%s";
    public static final String GET_INTERN_BOARD = BASE_URL + "fetchActivity.php?getInternboard=true&id=%s";
    public static final String GET_PHOTOS_URL = BASE_URL + "fetchActivity.php?getSharedPhotoList=true&id=%d";
    public static final String GET_ENGAGEMENTS_URL = BASE_URL + "fetchActivity.php?getEngagementsList=true&id=%d";
    public static final String GET_BOOKINGS_URL = BASE_URL + "fetchActivity.php?getBookingList=true&id=%d";
    public static final String GET_CHECKINS_URL = BASE_URL + "fetchActivity.php?getCheckedInList=true&id=%d";
    public static final String GET_ALL_REWARDS_URL = BASE_URL + "fetchActivity.php?fetchAll=set&id=%d";
    public static final String DELETE_BOOKMARK_URL = BASE_URL + "api.php?func_name=remove_event_from_bookmark&user_id=%d&event_id=%d";
    public static final String CLICK_COUNT_EVENT_URL = BASE_URL + "api.php?func_name=event_popular&event_id=%d";

    public static final String SHARE_EVENT_URL = BASE_URL + "user_api.php?insertActivity=true&event=%d&user=%d&ruleid=%d&activity_detail=%s&image_url=%s&ticket_count=%d";
    public static final String SHARE_EVENT_LOGIN_COUNT_URL = BASE_URL + "user_api.php?insertActivity=true&event=%d&user=%d&ruleid=%d&activity_detail=%s&ticket_count=%d";
    public static final String FROMDATE = "fromdate";
    public static final String TODATE = "todate";
    public static final String FILTERCITY = "filtercity";
    public static final String FILTEREVENTTYPE = "filtereventtype";
    public static final String FILTEREVENTTYPECATEGORY = "filtereventtypecategory";
    public static final String FILTERCAT = "filtercat";
    public static final String FILTERCITYINDEX = "filtercityindex";
    public static final String FILTEREVENTTYPEINDEX = "filtereventtypeindex";
    public static final String FILTEREVENTTYPECATEGORYINDEX = "filtereventtypecategoryindex";

    //Service Params
    public static String PARAM_MESSAGE = "msg";
    public static final String DEFAULT_PASSWORD = "heyla123";

    // Alert Dialog Constants
    public static String ALERT_DIALOG_TITLE = "alertDialogTitle";
    public static String ALERT_DIALOG_MESSAGE = "alertDialogMessage";
    public static String ALERT_DIALOG_TAG = "alertDialogTag";
    public static String ALERT_DIALOG_INPUT_HINT = "alert_dialog_input_hint";
    public static String ALERT_DIALOG_POS_BUTTON = "alert_dialog_pos_button";
    public static String ALERT_DIALOG_NEG_BUTTON = "alert_dialog_neg_button";

    // Signup params
    public static final String PARAMS_FUNC_NAME = "func_name";
    public static final String PARAMS_USER_NAME = "user_email";
    public static final String PARAMS_USER_ID = "user_id";
    public static final String PARAMS_USER_TYPE = "user_type";
    public static final String PARAMS_NAME = "user_name";
    public static final String PARAMS_SIGN_UP_TYPE = "sign_up_type";
    public static final String GCM_KEY = "gcm_key";
    public static final String MOBILE_TYPE = "mobile_type";
    public static final String PARAMS_USER_PASSWORD = "user_password";

    // Booking tickets params
//    public static final String PARAMS_FUNC_NAME = "func_name";

    // Preferences
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_GCM_ID = "gcm_id";
    public static final String KEY_IMEI = "imei_code";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_CITY = "city";
    public static final String KEY_USER_COUNTRY = "country";
    public static final String KEY_USER_HAS_PREFERENCES = "hasPreferences";
    public static final String KEY_USER_PHONE = "phone";
    public static final String KEY_LOGIN_MODE = "loginMode";
    public static final String KEY_FORGOT_PASSWORD_STATUS = "forgot_password_status";
    public static final String KEY_FACEBOOK_URL = "profileUrl";
    public static final String KEY_TWITTER_LOGGED_IN = "twitterLoggedIn";
    public static final String KEY_USER_GENDER = "gender";
    public static final String KEY_USER_PROMOCODE = "promocode";
    public static final String KEY_USER_BIRTHDAY = "birthday";
    public static final String KEY_USER_OCCUPATION = "occupation";
    public static final String KEY_SOCIAL_NETWORK_URL = "socialNetworkPicUrl";
    public static final String KEY_LAST_SHARED_TIME = "timeEventShared";
    public static final String KEY_EVENT_SHARED_COUNT = "eventSharedCount";
    public static final String ISFILTERAPPLY = "ISFILTERAPPLY";
    public static final String SINGLEDATEFILTER = "SINGLEDATEFILTER";

    //Loginmode constants
    public static final int FACEBOOK = 1;
    public static final int NORMAL_SIGNUP = 2;
    public static final int GOOGLE_PLUS = 3;

    public static final long TWENTY4HOURS = 24 * 60 * 60 * 1000;//24 hours in milli seconds format

    //Twitter Consumer keys and secrets
    public static final String TWITTER_CONSUMER_KEY = "K1CFiGhSKSeh93AfmDiDqIbgs";
    public static final String TWITTER_CONSUMER_SECRET = "oSBnfAI3XTnm8lgmoJ82ptnt9kfV9pbeTLTLgfMIeJJpKx60Gy";
    public static final String TWITTER_CALLBACK_URL = "oauth://com.findafun";

    // Twitter oauth urls
    public static final String URL_TWITTER_AUTH = "auth_url";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN = "TWITTER_OAUTH_TOKEN";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET = "TWITTER_OAUTH_TOKEN_SECRET";

    // Google Project Number
    public static final String GOOGLE_PROJECT_ID = "41690620391";
    public static final String MESSAGE_KEY = "message";

    // Booking Status

    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_PAYMENT_AMOUNT = "eventRate";
    public static final String KEY_TRANSACTION_DATE = "eventDate";


}

