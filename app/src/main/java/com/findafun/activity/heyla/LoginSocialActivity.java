package com.findafun.activity.heyla;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.findafun.R;
import com.findafun.activity.LandingActivity;
import com.findafun.activity.LoginNewActivity;
import com.findafun.activity.SelectCityActivity;
import com.findafun.activity.glogin.CustomVolleyRequest;
import com.findafun.adapter.LoginNewAdapter;
import com.findafun.adapter.SignUpNewAdapter;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.GCM.GCMRegistrationIntentService;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.ISignUpServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.findafun.fragment.LoginFragment.REG_ID;

/**
 * Created by Nandha on 11-03-2017.
 */

public class LoginSocialActivity extends AppCompatActivity implements DialogClickListener, GoogleApiClient.ConnectionCallbacks, ISignUpServiceListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginSocialActivity.class.getName();

    Context context;
    String regId;
    GoogleCloudMessaging gcm;
    private static final String APP_VERSION = "appVersion";
    //    private ProgressDialogHelper progressDialogHelper;
    private ProgressDialog mProgressDialog = null;
    private boolean mSignInClicked;
    private CallbackManager callbackManager;
    private boolean mResolvingError = false;
    private static final int RC_SIGN_IN = 100;
    private static final int REQUEST_CODE_TOKEN_AUTH = 1;
    private ConnectionResult mConnectionResult;
    String IMEINo;
    private String logType = "login";

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private SignUpServiceHelper signUpServiceHelper;

    private int mSelectedLoginMode = 0;

    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
//    private int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_social);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        progressDialogHelper = new ProgressDialogHelper(this);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEINo = telephonyManager.getDeviceId();

        PreferenceStorage.saveIMEI(getApplicationContext(), IMEINo);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.palprotech.heyla",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        logType = getIntent().getStringExtra("LoginType");

        context = getApplicationContext();

        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    regId = intent.getStringExtra("token");

                    PreferenceStorage.saveGCM(getApplicationContext(), regId);
                    //Displaying the token as toast
//                    Toast.makeText(getApplicationContext(), "Registration token:" + regId, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    //     Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    //    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                // Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                // Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        Log.i(TAG,
                "Displaying contacts permission rationale to provide additional context.");

        signUpServiceHelper = new SignUpServiceHelper(getApplicationContext());
        signUpServiceHelper.setSignUpServiceListener(this);
//        progressDialogHelper = new ProgressDialogHelper(getApplicationContext());

        FacebookSdk.sdkInitialize(getApplicationContext());
        initFacebook();

        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        UserLogin();

    }

    //This function will option signing intent
    private void signIn() {

//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Signing in...");
//        mProgressDialog.show();

        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Login with facebook
    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        Log.d(TAG, "Initializing facebook");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook Login Registration success");
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                        } else {
                                            String email = me.optString("email");
                                            String id = me.optString("id");
                                            String name = me.optString("name");
                                            String gender = me.optString("gender");
                                            String birthday = me.optString("birthday");
                                            Log.d(TAG, "facebook gender" + gender + "birthday" + birthday);
                                            PreferenceStorage.saveUserEmail(getApplicationContext(), email);
                                            PreferenceStorage.saveUserName(getApplicationContext(), name);
                                            String url = "https://graph.facebook.com/" + id + "/picture?type=large";
                                            Log.d(TAG, "facebook birthday" + birthday);
                                            PreferenceStorage.saveSocialNetworkProfilePic(getApplicationContext(), url);
                                            if (gender != null) {
                                                PreferenceStorage.saveUserGender(getApplicationContext(), gender);
                                            }
                                            if (birthday != null) {
                                                PreferenceStorage.saveUserBirthday(getApplicationContext(), birthday);
                                            }
                                            // send email and id to your web server
                                            JSONObject jsonObject = new JSONObject();
                                            Log.d(TAG, "Received Facebook profile" + me.toString());
                                            try {
                                                jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_in");
                                                jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, email);
                                                jsonObject.put(FindAFunConstants.PARAMS_USER_PASSWORD, FindAFunConstants.DEFAULT_PASSWORD);
                                                jsonObject.put(FindAFunConstants.PARAMS_SIGN_UP_TYPE, "1");
                                                jsonObject.put(FindAFunConstants.MOBILE_TYPE, "1");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
//                                            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                                            signUpServiceHelper.makeSignUpServiceCall(jsonObject.toString());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,email,name,link,birthday,gender");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e(TAG, "" + exception.toString());
                    }
                });
    }

    private void UserLogin() {

        if (logType.equalsIgnoreCase("1")) {
            String GCMKey = PreferenceStorage.getGCM(getApplicationContext());
            String IMEI = PreferenceStorage.getIMEI(getApplicationContext());
            PreferenceStorage.saveUserType(getApplicationContext(), "2");

//            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Signing in...");
            mProgressDialog.show();

            signUpServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GUEST_LOGIN, IMEI, GCMKey, "1"));

        } else if (logType.equalsIgnoreCase("2")) {

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Signing in...");
            mProgressDialog.show();

            Log.d(TAG, "start Facebook for logging in");
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
            PreferenceStorage.saveLoginMode(getApplicationContext(), FindAFunConstants.FACEBOOK);
            mSelectedLoginMode = FindAFunConstants.FACEBOOK;
            PreferenceStorage.saveUserType(getApplicationContext(), "1");

        } else if (logType.equalsIgnoreCase("3")) {

//            Log.d(TAG, "start Google plus for logging in");
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
//            PreferenceStorage.saveLoginMode(getApplicationContext(), FindAFunConstants.FACEBOOK);
//            mSelectedLoginMode = FindAFunConstants.FACEBOOK;
//            PreferenceStorage.saveUserType(getApplicationContext(), "1");

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Signing in...");
            mProgressDialog.show();

            PreferenceStorage.saveLoginMode(this, FindAFunConstants.GOOGLE_PLUS);
            PreferenceStorage.saveUserType(this, "1");
            mSelectedLoginMode = FindAFunConstants.GOOGLE_PLUS;
            signIn();

       /*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PreferenceStorage.saveLoginMode(this, FindAFunConstants.GOOGLE_PLUS);
                PreferenceStorage.saveUserType(this, "1");
                // mSelectedLoginMode = FindAFunConstants.FACEBOOK;
                mSelectedLoginMode = FindAFunConstants.GOOGLE_PLUS;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "initiate google plus sign up");
//                    initiateGplusSignIn();
                    signIn();
                } else {
                    Log.d(TAG, "check google permissions");
//                    checkPermissions();
                }
            } else {
                Log.d(TAG, "initiate google plus Sign in");
//                initiateGplusSignIn();
                signIn();
            } */


        }
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("SplashScreen", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("SplashScreen", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (mConnectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mResolvingError = true;
            }
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(FindAFunConstants.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                LandingActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            //If signin
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                //Calling a new function to handle signin
                handleSignInResult(result);
            }

//            mResolvingError = false;
//            if (resultCode == RESULT_OK) {
//                // If we have a successful result, we will want to be able to
//                // resolve any further errors, so turn on resolution with our
//                // flag.
//                mSignInClicked = true;
//                // If we have a successful result, lets call connect() again. If
//                // there are any more errors to resolve we'll get our
//                // onConnectionFailed, but if not, we'll get onConnected.
//                //mGoogleApiClient.connect();
//            } else if (resultCode != RESULT_OK) {
//                // If we've got an error we can't resolve, we're no
//                // longer in the midst of signing in, so we can stop
//                // the progress spinner.
//
////                progressDialogHelper.hideProgressDialog();
//            }

        }
//        else if (requestCode == REQUEST_CODE_TOKEN_AUTH) {
//            if (resultCode == RESULT_OK) {
//            }
//        }
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {

        try {
            //If the login succeed
            if (result.isSuccess()) {
                //Getting google account
                GoogleSignInAccount acct = result.getSignInAccount();

                String personName = acct.getDisplayName();
                String email = acct.getEmail();

                Log.e("", "Name: " + personName + ", plusProfile: "
                        + ", email: " + email
                        + ", Image: ");
                if (email != null) {
                    PreferenceStorage.saveUserEmail(this, email);
                }
                if (personName != null) {
                    PreferenceStorage.saveUserName(this, personName);
                }

                PreferenceStorage.saveSocialNetworkProfilePic(this, acct.getPhotoUrl().toString());
//                PreferenceStorage.saveLoginMode(this, FindAFunConstants.GOOGLE_PLUS);
//                PreferenceStorage.saveUserType(getApplicationContext(), "1");

                //            Log.d(TAG, "start Google plus for logging in");
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
//            PreferenceStorage.saveLoginMode(getApplicationContext(), FindAFunConstants.FACEBOOK);
//            mSelectedLoginMode = FindAFunConstants.FACEBOOK;
//            PreferenceStorage.saveUserType(getApplicationContext(), "1");

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_in");
                    jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, email);
                    jsonObject.put(FindAFunConstants.PARAMS_USER_PASSWORD, FindAFunConstants.DEFAULT_PASSWORD);
                    jsonObject.put(FindAFunConstants.PARAMS_SIGN_UP_TYPE, "1");
                    jsonObject.put(FindAFunConstants.MOBILE_TYPE, "1");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                signUpServiceHelper.makeSignUpServiceCall(jsonObject.toString());
                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X

                //Displaying name and email
//            textViewName.setText(acct.getDisplayName());
//            textViewEmail.setText(acct.getEmail());

                //Initializing image loader
//            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext()).getImageLoader();

//            imageLoader.get(acct.getPhotoUrl().toString(), ImageLoader.getImageListener(profilePhoto, R.mipmap.appicon_logo, R.mipmap.appicon_logo));

                //Loading image
//            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);

            } else {
                //If login fails
                Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            }

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

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInsuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(FindAFunConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInsuccess = false;
                        Log.d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(getApplicationContext(), msg);

                    } else {
                        signInsuccess = true;

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return signInsuccess;
    }

    @Override
    public void onSignUp(JSONObject response) {

//        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONObject userData = response.getJSONObject("userData");
                String user_id = null;
                Log.d(TAG, "userData dictionary" + userData.toString());
                if (userData != null) {
                    user_id = userData.getString("id");
//                    Toast.makeText(getActivity(), "Registration token:" + user_id, Toast.LENGTH_LONG).show();
                    PreferenceStorage.saveUserId(getApplicationContext(), userData.getString("id"));

                    Log.d(TAG, "created user id" + user_id);

                    //need to re do this
                    Log.d(TAG, "sign in response is" + response.toString());


                    if (PreferenceStorage.getUserType(getApplicationContext()).equalsIgnoreCase("1")) {

                        String name = userData.getString("name");
                        String userEmail = userData.getString("user_name");
                        // String pwd = userData.getString(FindAFunConstants.PARAMS_USER_PASSWORD);
                        String phone = userData.getString("phone");
                        String gender = userData.getString("gender");
                        String birthday = userData.getString("birthday");
                        String city = userData.getString("city_name");
                        String country = userData.getString("country_name");
                        String occupation = userData.getString("occupation");
                        String promocode = userData.getString("promocode");
                        String userImageUrl = userData.getString("user_image");
                        if ((name != null) && !(name.isEmpty()) && !name.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserName(getApplicationContext(), name);
                        }
                        if ((userEmail != null) && !(userEmail.isEmpty()) && !userEmail.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserEmail(getApplicationContext(), userEmail);

                        }
                    /*if((pwd != null) && !(pwd.isEmpty()) && !pwd.equalsIgnoreCase("null") ){
                        PreferenceStorage.savePassword(this, pwd);

                    }*/
                        if ((phone != null) && !(phone.isEmpty()) && !phone.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserPhone(getApplicationContext(), phone);
                        }
                        if ((gender != null) && !(gender.isEmpty()) && !gender.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserGender(getApplicationContext(), gender);
                        }
                        if ((birthday != null) && !(birthday.isEmpty()) && !birthday.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserBirthday(getApplicationContext(), birthday);
                        }
                        if ((city != null) && !(city.isEmpty()) && !(city.equalsIgnoreCase("0")) && !city.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserCity(getApplicationContext(), city);
                        }
                        if ((country != null) && !(country.isEmpty()) && !(country.equalsIgnoreCase("0")) && !country.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserCountry(getApplicationContext(), country);
                        }
                        if ((occupation != null) && !(occupation.isEmpty()) && !(occupation.equalsIgnoreCase("0")) && !occupation.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserOccupation(getApplicationContext(), occupation);
                        }
                        if ((promocode != null) && !(promocode.isEmpty()) && !promocode.equalsIgnoreCase("null")) {
                            PreferenceStorage.savePromoCode(getApplicationContext(), promocode);
                        }
                        if ((userImageUrl != null) && !(userImageUrl.isEmpty()) && !userImageUrl.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveProfilePic(getApplicationContext(), userImageUrl);
                        }

                    } else {
                        String name = userData.getString("name");
                        String userEmail = userData.getString("user_name");

                        if ((name != null) && !(name.isEmpty()) && !name.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserName(getApplicationContext(), name);
                        }
                        if ((userEmail != null) && !(userEmail.isEmpty()) && !userEmail.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserEmail(getApplicationContext(), userEmail);

                        }
                    }


                    //

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mProgressDialog != null) {
                mProgressDialog.cancel();
            }

            //clear out data for old Login
            GamificationDataHolder.getInstance().clearGamificationData();
            Intent intent = new Intent(getApplicationContext(), SelectCityActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "Error while sign In");
        }

    }

    @Override
    public void onSignUpError(String error) {
//        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Log.d(TAG, "OnCOnnected");

        // Hide the progress dialog if its showing.
        // Toast.makeText(this, "User is connected !", Toast.LENGTH_SHORT).show();
        // Get user's information
//        progressDialogHelper.showProgressDialog("Signing in...");
    }

    @Override
    public void onConnectionSuspended(int i) {
//        progressDialogHelper.hideProgressDialog();
        Toast.makeText(this, "User is onConnectionSuspended!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        progressDialogHelper.hideProgressDialog();
        Log.d(TAG, "Google api connection failed");
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }
        if (!mResolvingError) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }
}
