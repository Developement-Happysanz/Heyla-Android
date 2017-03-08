package com.findafun.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.findafun.R;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.ISignUpServiceListener;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, ISignUpServiceListener, DialogClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = SignUpActivity.class.getName();

    private static final int RC_SIGN_IN = 0;
    private static final int REQUEST_CODE_TOKEN_AUTH = 1;
    private static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 100;
    private CallbackManager callbackManager;
    private Button btnFacebook, btnGPlus;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            //We get a connection to the Google Play Service API
            // Initializing google plus api client
            Log.d(TAG, "Initiating google plus connection");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();


        } else {
            GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this),
                    this,
                    REQUEST_SOLVE_CONNECTION);
        }

        initializeViews();
        // Initialize Facebook SDK
        generateFacebookKeys();
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        initFacebook();

        signUpServiceHelper = new SignUpServiceHelper(this);
        signUpServiceHelper.setSignUpServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

    }

    private void generateFacebookKeys() {
        Log.d(TAG, "Generating facebook has keys");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "world.of.fun",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
            Log.d(TAG, "Finished key hashing");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Initialize Views
    private void initializeViews() {
        btnFacebook = (Button) findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(this);
        btnGPlus = (Button) findViewById(R.id.btnGplus);
        btnGPlus.setOnClickListener(this);
        btnSignIn = (Button) findViewById(R.id.btn_sign_up);
        btnSignIn.setOnClickListener(this);
        edtUserName = (EditText) findViewById(R.id.editText_email);
        edtPassword = (EditText) findViewById(R.id.editText_password);
        name = (EditText) findViewById(R.id.editText_name);

        TextView termsView = (TextView) findViewById(R.id.txt_terms_and_conditions);
        SpannableString string = new SpannableString("Terms and conditions");
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        termsView.setText(string);
        termsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);

            }
        });

        TextView alreadyHaveaccount = (TextView) findViewById(R.id.account_exists);
        alreadyHaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Login with facebook
    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        Log.d(TAG, "Initializing facebook");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
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
                                            PreferenceStorage.saveUserEmail(SignUpActivity.this, email);
                                            PreferenceStorage.saveUserName(SignUpActivity.this, name);
                                            String url = "https://graph.facebook.com/" + id + "/picture?type=large";
                                            Log.d(TAG, "facebook url" + url);
                                            PreferenceStorage.saveSocialNetworkProfilePic(SignUpActivity.this, url);
                                            // send email and id to your web server
                                            JSONObject jsonObject = new JSONObject();
                                            Log.d(TAG, "Received Facebook profile" + me.toString());
                                            try {
                                                jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_up_latest");
                                                jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, email);
                                                jsonObject.put("user_password", FindAFunConstants.DEFAULT_PASSWORD);
                                                jsonObject.put("user_name", name);
                                                jsonObject.put(FindAFunConstants.PARAMS_SIGN_UP_TYPE, "1");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                                            signUpServiceHelper.makeSignUpServiceCall(jsonObject.toString());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,email,name,link");
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
                        Log.e("Facebook", "" + exception.toString());
                    }
                });
    }


    @Override
    public void onClick(View view) {

        if (CommonUtils.isNetworkAvailable(this)) {
            if (view == btnFacebook) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
                PreferenceStorage.saveLoginMode(this, FindAFunConstants.FACEBOOK);
                mSelectedLoginMode = FindAFunConstants.FACEBOOK;
            } else if (view == btnSignIn) {

                if (validateFields()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_up_latest");
                        jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, edtUserName.getText().toString());
                        jsonObject.put("user_password", edtPassword.getText().toString());
                        //jsonObject.put(FindAFunConstants.PARAMS_USER_CITY, edtPassword.getText().toString());
                        jsonObject.put("user_name", name.getText().toString());
                        jsonObject.put(FindAFunConstants.PARAMS_SIGN_UP_TYPE, "2");
                        mSelectedLoginMode = FindAFunConstants.NORMAL_SIGNUP;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    signUpServiceHelper.makeSignUpServiceCall(jsonObject.toString());
                    PreferenceStorage.saveLoginMode(this, 2);
                }
            } else if (view == txtSignUp) {
                Intent signUpIntent = new Intent(this, SignUpActivity.class);
                startActivity(signUpIntent);
            } else if (view == btnGPlus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PreferenceStorage.saveLoginMode(this, FindAFunConstants.GOOGLE_PLUS);
                    mSelectedLoginMode = FindAFunConstants.FACEBOOK;
                    mSelectedLoginMode = FindAFunConstants.GOOGLE_PLUS;
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
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

            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // If we have a successful result, we will want to be able to
                // resolve any further errors, so turn on resolution with our
                // flag.
                mSignInClicked = true;
                // If we have a successful result, lets call connect() again. If
                // there are any more errors to resolve we'll get our
                // onConnectionFailed, but if not, we'll get onConnected.
                mGoogleApiClient.connect();
            } else if (resultCode != RESULT_OK) {
                // If we've got an error we can't resolve, we're no
                // longer in the midst of signing in, so we can stop
                // the progress spinner.
                progressDialogHelper.hideProgressDialog();
            }

        } else if (requestCode == REQUEST_CODE_TOKEN_AUTH) {
            if (resultCode == RESULT_OK) {
            }
        }
    }


    private boolean validateFields() {
        if (!FindAFunValidator.checkNullString(this.name.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.enter_user_name));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.edtUserName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.email_empty_str));
            return false;
        } else if (!FindAFunValidator.isEmailValid(this.edtUserName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.enter_valid_email));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.edtPassword.getText().toString())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.enter_password));
            return false;
        } else if (!FindAFunValidator.withinPermittedLength(this.edtPassword.getText().toString())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Password legth should be greater than 6 characters");
            return false;
        } else {
            return true;
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
                        if (status.equalsIgnoreCase("activationError")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            //alertDialogBuilder.setTitle("Registration Successful");
                            alertDialogBuilder.setMessage(msg);
                            alertDialogBuilder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            Log.d(TAG, "Show error dialog");
                            AlertDialogHelper.showSimpleAlertDialog(this, msg);
                        }

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
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Registration Successful");


            alertDialogBuilder.setMessage("Activation Link sent to your email. Activate it and perform Sign In");
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            /*Intent intent = new Intent(this, SelectCityActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();*/
        }
    }

    @Override
    public void onSignUpError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

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
        Toast.makeText(this, "User is onConnectionSuspended!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        progressDialogHelper.hideProgressDialog();
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
                mGoogleApiClient.connect();
            }
        }
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
                            getApplicationContext(),
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
                    PreferenceStorage.saveUserEmail(SignUpActivity.this, email);
                }
                if (personName != null) {
                    PreferenceStorage.saveUserName(SignUpActivity.this, personName);
                }

                PreferenceStorage.saveSocialNetworkProfilePic(SignUpActivity.this, personPhotoUrl);
                PreferenceStorage.saveLoginMode(SignUpActivity.this, FindAFunConstants.GOOGLE_PLUS);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_up_latest");
                    jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, email);
                    jsonObject.put(FindAFunConstants.PARAMS_USER_PASSWORD, FindAFunConstants.DEFAULT_PASSWORD);
                    jsonObject.put("user_name", personName);
                    jsonObject.put(FindAFunConstants.PARAMS_SIGN_UP_TYPE, "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                signUpServiceHelper.makeSignUpServiceCall(jsonObject.toString());
                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X


            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.GET_ACCOUNTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
