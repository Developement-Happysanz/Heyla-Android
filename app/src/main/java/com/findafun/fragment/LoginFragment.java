package com.findafun.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.findafun.R;
import com.findafun.activity.ForgotPasswordActivity;
import com.findafun.activity.LandingActivity;
import com.findafun.activity.LoginActivity;
import com.findafun.activity.SelectCityActivity;
import com.findafun.activity.SelectPreferenceActivity;
import com.findafun.activity.SignUpActivity;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.ISignUpServiceListener;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;
import com.findafun.utils.PermissionUtil;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, DialogClickListener, ISignUpServiceListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = LoginActivity.class.getName();

    //Login
    private static final int RC_SIGN_IN = 0;
    private static final int REQUEST_CODE_TOKEN_AUTH = 1;
    private static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 100;
    private static final int REQUEST_PERMISSION_All = 111;
    private static String[] PERMISSIONS_ALL = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private View mLayout;
    private CallbackManager callbackManager;
    private ImageView btnFacebook, btnGPlus, txtGuestLogin; //btnFacebook
    //LoginButton btnFacebook;
    private Button btnSignIn;
    private SignUpServiceHelper signUpServiceHelper;
    private EditText edtUserName, edtPassword, name, city;
    private ProgressDialogHelper progressDialogHelper;
    private TextView txtSignUp;
    private GoogleApiClient mGoogleApiClient;
    private int REQUEST_SOLVE_CONNECTION = 999;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private boolean mResolvingError = false;
    private SharedPreferences mSharedPreferences;
    private View mDecorView;
    private int mSelectedLoginMode = 0;
    public View view;
    public View viewResult;

    public static final String REG_ID = "regId";

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        if (PreferenceStorage.getUserId(getActivity()) != null && FindAFunValidator.checkNullString(PreferenceStorage.getUserId(getActivity()))) {
            String city = PreferenceStorage.getUserCity(getActivity());
            boolean haspreferences = PreferenceStorage.isPreferencesPresent(getActivity());
            if (FindAFunValidator.checkNullString(city) && haspreferences) {
                Intent intent = new Intent(getActivity(), LandingActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else if (!FindAFunValidator.checkNullString(city)) {
                Log.d(TAG, "No city yet, show city activity");
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();

            } else if (!haspreferences) {
                Log.d(TAG, "No preferences, so launch preferences activity");
                Intent intent = new Intent(getActivity(), SelectPreferenceActivity.class);
                intent.putExtra("selectedCity", city);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                getActivity().finish();
            }
        } else {

            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) == ConnectionResult.SUCCESS) {
                //We get a connection to the Google Play Service API
                // Initializing google plus api client
                Log.d(TAG, "Initiating google plus connection");
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Plus.API)
                        .addScope(Plus.SCOPE_PLUS_LOGIN)
                        .build();


            } else {
                GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()),
                        getActivity(),
                        REQUEST_SOLVE_CONNECTION);
            }

            viewResult = initializeViews(view);

            FacebookSdk.sdkInitialize(getActivity());
            initFacebook();
            btnFacebook.setOnClickListener(this);
            btnGPlus.setOnClickListener(this);
            btnSignIn.setOnClickListener(this);
            txtGuestLogin.setOnClickListener(this);

            signUpServiceHelper = new SignUpServiceHelper(getActivity());
            signUpServiceHelper.setSignUpServiceListener(this);
            progressDialogHelper = new ProgressDialogHelper(getActivity());
        }

        // Inflate the layout for this fragment
        return viewResult;
    }

    // Initialize Views
    private View initializeViews(View view) {
        mLayout = view.findViewById(R.id.activity_new_login);

        btnFacebook = (ImageView) view.findViewById(R.id.frag_login_fb);

        btnGPlus = (ImageView) view.findViewById(R.id.frag_login_google);


        btnSignIn = (Button) view.findViewById(R.id.btn_sign_in);

        edtUserName = (EditText) view.findViewById(R.id.editText_email);
        edtPassword = (EditText) view.findViewById(R.id.editText_password);

        txtGuestLogin = (ImageView) view.findViewById(R.id.text_guest);
        //  name = (EditText) findViewById(R.id.editText_name);

        TextView termsView = (TextView) view.findViewById(R.id.txt_terms_and_conditions);
        SpannableString string = new SpannableString("Terms and conditions");
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);

        TextView forgotPassword = (TextView) view.findViewById(R.id.text_forgot);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

//        TextView skipLogin = (TextView) view.findViewById(R.id.text_guest);
//        skipLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
//                startActivity(intent);
//                getActivity().finish();
//            }
//        });

        // txtSignUp = (TextView) findViewById(R.id.txt_sign_up);
        //txtSignUp.setOnClickListener(this);
        return view;
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
                                            PreferenceStorage.saveUserEmail(getActivity(), email);
                                            PreferenceStorage.saveUserName(getActivity(), name);
                                            String url = "https://graph.facebook.com/" + id + "/picture?type=large";
                                            Log.d(TAG, "facebook birthday" + birthday);
                                            PreferenceStorage.saveSocialNetworkProfilePic(getActivity(), url);
                                            if (gender != null) {
                                                PreferenceStorage.saveUserGender(getActivity(), gender);
                                            }
                                            if (birthday != null) {
                                                PreferenceStorage.saveUserBirthday(getActivity(), birthday);
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
                                            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
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

    @Override
    public void onClick(View view) {
        //check if network connection exists
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            if (view == btnFacebook) {
                Log.d(TAG, "start Facebook for logging in");
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
                PreferenceStorage.saveLoginMode(getActivity(), FindAFunConstants.FACEBOOK);
                mSelectedLoginMode = FindAFunConstants.FACEBOOK;
                PreferenceStorage.saveUserType(getActivity(), "1");
            } else if (view == btnSignIn) {

                if (validateFields()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_in");
                        jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, edtUserName.getText().toString());
                        jsonObject.put("user_password", edtPassword.getText().toString());
                        //jsonObject.put(FindAFunConstants.PARAMS_USER_CITY, edtPassword.getText().toString());
                        // jsonObject.put(FindAFunConstants.PARAMS_NAME, name.getText().toString());
                        jsonObject.put(FindAFunConstants.PARAMS_SIGN_UP_TYPE, "2");
                        String GCMKey = PreferenceStorage.getGCM(getActivity());
                        jsonObject.put(FindAFunConstants.GCM_KEY, GCMKey);
                        jsonObject.put(FindAFunConstants.MOBILE_TYPE, "1");
                        mSelectedLoginMode = FindAFunConstants.NORMAL_SIGNUP;
                        PreferenceStorage.savePassword(getActivity(), edtPassword.getText().toString());
                        PreferenceStorage.saveUserType(getActivity(), "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    signUpServiceHelper.makeSignUpServiceCall(jsonObject.toString());
                    PreferenceStorage.saveLoginMode(getActivity(), 2);
                }
            } else if (view == txtSignUp) {
                Intent signUpIntent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(signUpIntent);
            } else if (view == btnGPlus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PreferenceStorage.saveLoginMode(getActivity(), FindAFunConstants.GOOGLE_PLUS);
                    PreferenceStorage.saveUserType(getActivity(), "1");
                    // mSelectedLoginMode = FindAFunConstants.FACEBOOK;
                    mSelectedLoginMode = FindAFunConstants.GOOGLE_PLUS;
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "initiate google plus sign up");
                        initiateGplusSignIn();
                    } else {
                        Log.d(TAG, "check google permissions");
                        checkPermissions();
                    }
                } else {
                    Log.d(TAG, "initiate google plus Sign in");
                    initiateGplusSignIn();
                }
            } else if (view == txtGuestLogin) {

                String GCMKey = PreferenceStorage.getGCM(getActivity());
                String IMEI = PreferenceStorage.getIMEI(getActivity());
                PreferenceStorage.saveUserType(getActivity(), "2");

                signUpServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GUEST_LOGIN, IMEI, GCMKey, "1"));

                // Toast.makeText(getActivity(), "Registration token:" + regId, Toast.LENGTH_LONG).show();
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), "No Network connection");
        }
    }


    private void initiateGplusSignIn() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Not yet connected");
//                Show the dialog as we are now signing in.
            progressDialogHelper.showProgressDialog("Signing in...");
            // Make sure that we will start the resolution (e.g. fire the
            // intent and pop up a dialog for the user) for any errors
            // that come in.
            mSignInClicked = true;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (mConnectionResult != null) {
                Log.d(TAG, "resolving google plus SignIn error");
                resolveSignInError();
            } else {
                Log.d(TAG, "No connection yet. connecting");
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                mGoogleApiClient.connect();
            }
        } else {
            Log.d(TAG, "Google plus signing in");
            progressDialogHelper.showProgressDialog("Signing in...");
            Log.d(TAG, "Get profile information");
            getProfileInformation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mResolvingError = false;
            if (resultCode == Activity.RESULT_OK) {
                // If we have a successful result, we will want to be able to
                // resolve any further errors, so turn on resolution with our
                // flag.
                mSignInClicked = true;
                // If we have a successful result, lets call connect() again. If
                // there are any more errors to resolve we'll get our
                // onConnectionFailed, but if not, we'll get onConnected.
                mGoogleApiClient.connect();
            } else if (resultCode != Activity.RESULT_OK) {
                // If we've got an error we can't resolve, we're no
                // longer in the midst of signing in, so we can stop
                // the progress spinner.

                progressDialogHelper.hideProgressDialog();
            }

        } else if (requestCode == REQUEST_CODE_TOKEN_AUTH) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
    }

    private boolean validateFields() {
        if (!FindAFunValidator.checkNullString(this.edtUserName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), this.getResources().getString(R.string.email_empty_str));
            return false;
        } else if (!FindAFunValidator.isEmailValid(this.edtUserName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), this.getResources().getString(R.string.enter_valid_email));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.edtPassword.getText().toString())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), this.getResources().getString(R.string.enter_password));
            return false;
        } else if (!FindAFunValidator.withinPermittedLength(this.edtPassword.getText().toString())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), "Incorrect password");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Log.d(TAG, "OnCOnnected");

        // Hide the progress dialog if its showing.
        // Toast.makeText(this, "User is connected !", Toast.LENGTH_SHORT).show();
        // Get user's information
        progressDialogHelper.showProgressDialog("Signing in...");
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        progressDialogHelper.hideProgressDialog();
        Toast.makeText(getActivity(), "User is onConnectionSuspended!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        progressDialogHelper.hideProgressDialog();
        Log.d(TAG, "Google api connection failed");
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(),
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
                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mResolvingError = true;
                mGoogleApiClient.connect();
            }
        }
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
                        AlertDialogHelper.showSimpleAlertDialog(getActivity(), msg);

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

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
//                    final  String CLIENT_ID = "961997472364-50gpv4ob4ngh89qdb4dek33b89husa0v.apps.googleusercontent.com";
//                    final  List<String> SCOPES = Arrays.asList(new String[]{
//                            "https://www.googleapis.com/auth/userinfo.profile",
//                            "https://www.googleapis.com/auth/userinfo.email"
//                    });
//                    String scope = String.format("oauth2:server:client_id:%s:api_scope:%s", CLIENT_ID, TextUtils.join(" ", SCOPES));
//                    token=GoogleAuthUtil.getToken(HomeActivity.this,Plus.AccountApi.getAccountName(mGoogleApiClient), scope);
                    token = GoogleAuthUtil.getToken(
                            getActivity(),
                            Plus.AccountApi.getAccountName(mGoogleApiClient), "oauth2:"
                                    +
                                    Scopes.PLUS_LOGIN);

                    Log.e("Access Token-->", token);

//			       /* Storing oAuth tokens to shared preferences */
//                    SharedPreferences.Editor e = mSharedPreferences.edit();
//                    e.putString(PREF_GPLUS_TOKEN, token);
//                    e.putString(PREF_PROVIDER, getResources().getString(R.string.provider_google));
//                    e.commit();
                } catch (IOException transientEx) {
                    // Network or server error, try later
                    Log.e("", transientEx.toString());
                } catch (UserRecoverableAuthException e) {
                    // Recover (with e.getIntent())
                    Log.e("", e.toString());
                    Intent recover = e.getIntent();
                    startActivityForResult(recover, REQUEST_CODE_TOKEN_AUTH);
                } catch (GoogleAuthException authEx) {
                    // The call is not ever expected to succeed
                    // assuming you have already verified that
                    // Google Play services is installed.
                    Log.e("", authEx.toString());
                } catch (Exception e) {
                    Log.e("", e.toString());
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                Log.i("", "Access token retrieved:" + token);
                // makeSignUpServiceCall(getResources().getString(R.string.provider_google));
            }

            @Override
            protected void onPreExecute() {
            }
        };
        task.execute();
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.d(TAG, "fetching the details from gmail account");
                  /* Storing oAuth tokens to shared preferences */
//                SharedPreferences.Editor e = mSharedPreferences.edit();
//                e.putString(PREF_GPLUS_EMAIL_ID, email);
//                e.commit();

                Log.e("", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                if (email != null) {
                    PreferenceStorage.saveUserEmail(getActivity(), email);
                }
                if (personName != null) {
                    PreferenceStorage.saveUserName(getActivity(), personName);
                }

                PreferenceStorage.saveSocialNetworkProfilePic(getActivity(), personPhotoUrl);
                PreferenceStorage.saveLoginMode(getActivity(), FindAFunConstants.GOOGLE_PLUS);

                //gender
                if (currentPerson.hasGender()) {
                    int gender = currentPerson.getGender();
                    Log.d(TAG, "gender value of gmail user" + gender);
                    if (gender == 0) {
                        PreferenceStorage.saveUserGender(getActivity(), "male");
                    } else {
                        PreferenceStorage.saveUserGender(getActivity(), "female");
                    }
                }
                //birthday
                if (currentPerson.hasBirthday()) {
                    Log.d(TAG, "gmail user birthday is" + currentPerson.getBirthday());
                }

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


            } else {
                Toast.makeText(getActivity(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

        }
    }

    /**
     * Revoking access from google
     */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e("", "User access revoked!");
                            mGoogleApiClient.connect();
                        }

                    });
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.GET_ACCOUNTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    initiateGplusSignIn();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case REQUEST_PERMISSION_All: {

                if (requestCode == REQUEST_PERMISSION_All) {
                    Log.i(TAG, "Received response for contact permissions request.");

                    // We have requested multiple permissions for contacts, so all of them need to be
                    // checked.
                    if (PermissionUtil.verifyPermissions(grantResults)) {
                        // All required permissions have been granted, display contacts fragment.
                        Snackbar.make(mLayout, R.string.permision_available_All,
                                Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        Log.i(TAG, "all permissions were NOT granted.");
                        Snackbar.make(mLayout, R.string.permissions_not_granted,
                                Snackbar.LENGTH_LONG)
                                .show();
                    }

                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onSignUp(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONObject userData = response.getJSONObject("userData");
                String user_id = null;
                Log.d(TAG, "userData dictionary" + userData.toString());
                if (userData != null) {
                    user_id = userData.getString("id");
//                    Toast.makeText(getActivity(), "Registration token:" + user_id, Toast.LENGTH_LONG).show();
                    PreferenceStorage.saveUserId(getActivity(), userData.getString("id"));

                    Log.d(TAG, "created user id" + user_id);

                    //need to re do this
                    Log.d(TAG, "sign in response is" + response.toString());


                    if (PreferenceStorage.getUserType(getActivity()).equalsIgnoreCase("1")) {

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
                            PreferenceStorage.saveUserName(getActivity(), name);
                        }
                        if ((userEmail != null) && !(userEmail.isEmpty()) && !userEmail.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserEmail(getActivity(), userEmail);

                        }
                    /*if((pwd != null) && !(pwd.isEmpty()) && !pwd.equalsIgnoreCase("null") ){
                        PreferenceStorage.savePassword(this, pwd);

                    }*/
                        if ((phone != null) && !(phone.isEmpty()) && !phone.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserPhone(getActivity(), phone);
                        }
                        if ((gender != null) && !(gender.isEmpty()) && !gender.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserGender(getActivity(), gender);
                        }
                        if ((birthday != null) && !(birthday.isEmpty()) && !birthday.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserBirthday(getActivity(), birthday);
                        }
                        if ((city != null) && !(city.isEmpty()) && !(city.equalsIgnoreCase("0")) && !city.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserCity(getActivity(), city);
                        }
                        if ((country != null) && !(country.isEmpty()) && !(country.equalsIgnoreCase("0")) && !country.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserCountry(getActivity(), country);
                        }
                        if ((occupation != null) && !(occupation.isEmpty()) && !(occupation.equalsIgnoreCase("0")) && !occupation.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserOccupation(getActivity(), occupation);
                        }
                        if ((promocode != null) && !(promocode.isEmpty()) && !promocode.equalsIgnoreCase("null")) {
                            PreferenceStorage.savePromoCode(getActivity(), promocode);
                        }
                        if ((userImageUrl != null) && !(userImageUrl.isEmpty()) && !userImageUrl.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveProfilePic(getActivity(), userImageUrl);

                        }

                    } else {
                        String name = userData.getString("name");
                        String userEmail = userData.getString("user_name");

                        if ((name != null) && !(name.isEmpty()) && !name.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserName(getActivity(), name);
                        }
                        if ((userEmail != null) && !(userEmail.isEmpty()) && !userEmail.equalsIgnoreCase("null")) {
                            PreferenceStorage.saveUserEmail(getActivity(), userEmail);

                        }
                    }


                    //

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //clear out data for old Login
            GamificationDataHolder.getInstance().clearGamificationData();
            Intent intent = new Intent(getActivity(), SelectCityActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onSignUpError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(getActivity(), error);
    }
}
