package com.findafun.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nandhakumar.k on 01/11/15.
 */
public class PreferenceStorage {

    public static void saveUserId(Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_ID, userId);
        editor.commit();
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.KEY_USER_ID, "");
        return userId;
    }

    public static void saveUserType(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_TYPE, userType);
        editor.commit();
    }

    public static String getUserType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.KEY_USER_TYPE, "");
        return userId;
    }

    public static void saveGCM(Context context, String gcmId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_GCM_ID, gcmId);
        editor.commit();
    }

    public static String getGCM(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userGCMId = sharedPreferences.getString(FindAFunConstants.KEY_GCM_ID, "");
        return userGCMId;
    }

    public static void saveIMEI(Context context, String imei) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_IMEI, imei);
        editor.commit();
    }

    public static String getIMEI(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userGCMId = sharedPreferences.getString(FindAFunConstants.KEY_IMEI, "");
        return userGCMId;
    }

    public static void saveUserName(Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_NAME, userId);
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.KEY_USER_NAME, "");
        return userId;
    }

    public static void savePassword(Context context, String pwd) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_PASSWORD, pwd);
        editor.commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.KEY_PASSWORD, "");
        return userId;
    }

    public static void saveUserEmail(Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_EMAIL, userId);
        editor.commit();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.KEY_USER_EMAIL, "");
        return userId;
    }

    public static void saveUserCity(Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_CITY, userId);
        editor.commit();
    }

    public static String getUserCity(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.KEY_USER_CITY, "");
        return userId;
    }

    public static void saveUserCountry(Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_COUNTRY, userId);
        editor.commit();
    }

    public static String getUserCountry(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.KEY_USER_COUNTRY, "");
        return userId;
    }

    public static void saveUserPhone(Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_PHONE, userId);
        editor.commit();
    }

    public static String getUserPhone(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.KEY_USER_PHONE, "");
        return userId;
    }

    public static void saveLoginMode(Context context, int type) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FindAFunConstants.KEY_LOGIN_MODE, type);
        editor.commit();

    }

    public static int getLoginMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Integer mode = sharedPreferences.getInt(FindAFunConstants.KEY_LOGIN_MODE, 0);
        return mode;

    }

    public static void saveProfilePic(Context context, String url) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_FACEBOOK_URL, url);
        editor.commit();

    }

    public static String getProfileUrl(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(FindAFunConstants.KEY_FACEBOOK_URL, "");
        return url;

    }

    public static String getForgotPasswordStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(FindAFunConstants.KEY_FORGOT_PASSWORD_STATUS, "");
        return url;
    }

    public static void saveForgotPasswordStatus(Context context, String url) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_FORGOT_PASSWORD_STATUS, url);
        editor.commit();
    }

    public static void saveSocialNetworkProfilePic(Context context, String url) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_SOCIAL_NETWORK_URL, url);
        editor.commit();

    }

    public static String getSocialNetworkProfileUrl(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(FindAFunConstants.KEY_SOCIAL_NETWORK_URL, "");
        return url;

    }

    public static void saveTwitterLoggedIn(Context context, boolean logged) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FindAFunConstants.KEY_TWITTER_LOGGED_IN, logged);
        editor.commit();

    }

    public static boolean getTwitterLoggedIn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean logged = sharedPreferences.getBoolean(FindAFunConstants.KEY_TWITTER_LOGGED_IN, false);
        return logged;

    }

    public static void savePreferencesSelected(Context context, boolean selected) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FindAFunConstants.KEY_USER_HAS_PREFERENCES, selected);
        editor.commit();

    }

    public static boolean isPreferencesPresent(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean logged = sharedPreferences.getBoolean(FindAFunConstants.KEY_USER_HAS_PREFERENCES, false);
        return logged;

    }

    public static void saveUserBirthday(Context context, String data) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_BIRTHDAY, data);
        editor.commit();

    }

    public static String getUserBirthday(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(FindAFunConstants.KEY_USER_BIRTHDAY, "");
        return url;

    }

    public static void saveUserGender(Context context, String data) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_GENDER, data);
        editor.commit();

    }

    public static String getUserGender(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(FindAFunConstants.KEY_USER_GENDER, "");
        return url;

    }

    public static void savePromoCode(Context context, String data) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_PROMOCODE, data);
        editor.commit();

    }

    public static String getPromoCode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(FindAFunConstants.KEY_USER_PROMOCODE, "");
        return url;

    }

    public static void saveUserOccupation(Context context, String data) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.KEY_USER_OCCUPATION, data);
        editor.commit();

    }

    public static String getUserOccupation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(FindAFunConstants.KEY_USER_OCCUPATION, "");
        return url;

    }

    public static void saveEventSharedtime(Context context, long millisecs) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(FindAFunConstants.KEY_LAST_SHARED_TIME, millisecs);
        editor.commit();

    }

    public static long getEventSharedTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Long mode = sharedPreferences.getLong(FindAFunConstants.KEY_LAST_SHARED_TIME, 0);
        return mode;

    }

    public static void saveEventSharedcount(Context context, int count) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FindAFunConstants.KEY_EVENT_SHARED_COUNT, count);
        editor.commit();

    }

    public static int getEventSharedcount(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Integer mode = sharedPreferences.getInt(FindAFunConstants.KEY_EVENT_SHARED_COUNT, 0);
        return mode;

    }

    public static void saveFilterSingleDate(Context context, String singledate) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.SINGLEDATEFILTER, singledate);
        editor.commit();
    }

    public static String getFilterSingleDate(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.SINGLEDATEFILTER, "");
        return userId;
    }

    public static void IsFilterApply(Context context, boolean IsFilterApply) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FindAFunConstants.ISFILTERAPPLY, IsFilterApply);
        editor.commit();

    }

    public static boolean getFilterApply(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean logged = sharedPreferences.getBoolean(FindAFunConstants.ISFILTERAPPLY, false);
        return logged;

    }

    public static void saveFilterFromDate(Context context, String singledate) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.FROMDATE, singledate);
        editor.commit();
    }

    public static String getFilterFromDate(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.FROMDATE, "");
        return userId;
    }

    public static void saveFilterToDate(Context context, String singledate) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.TODATE, singledate);
        editor.commit();
    }

    public static String getFilterToDate(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.TODATE, "");
        return userId;
    }

    public static void saveFilterEventType(Context context, String singledate) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.FILTEREVENTTYPE, singledate);
        editor.commit();
    }

    public static String getFilterEventType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.FILTEREVENTTYPE, "");
        return userId;
    }

    public static void saveFilterCity(Context context, String singledate) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.FILTERCITY, singledate);
        editor.commit();
    }

    public static String getFilterCity(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.FILTERCITY, "");
        return userId;
    }

    public static void saveFilterCitySelection(Context context, int index) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FindAFunConstants.FILTERCITYINDEX, index);
        editor.commit();
    }

    public static void saveFilterEventTypeSelection(Context context, int index) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FindAFunConstants.FILTEREVENTTYPEINDEX, index);
        editor.commit();
    }

    public static int getFilterCityIndex(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        int userId = sharedPreferences.getInt(FindAFunConstants.FILTERCITYINDEX, -1);
        return userId;
    }

    public static int getFilterEventTypeIndex(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        int userId = sharedPreferences.getInt(FindAFunConstants.FILTEREVENTTYPEINDEX, -1);
        return userId;
    }

    public static void saveFilterCatgry(Context context, String singledate) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FindAFunConstants.FILTERCAT, singledate);
        editor.commit();
    }

    public static String getFilterCatgry(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString(FindAFunConstants.FILTERCAT, "");
        return userId;
    }
}
