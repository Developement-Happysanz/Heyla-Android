package com.findafun.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
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
import com.findafun.activity.LoginNew;
import com.findafun.activity.LoginNewActivity;
import com.findafun.activity.SplashScreenActivity;
import com.findafun.activity.TermsAndConditionsActivity;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.ISignUpServiceListener;
import com.findafun.twitter.TwitterUtil;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements View.OnClickListener, ISignUpServiceListener, DialogClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SignupFragment.class.getName();

    int signUpClick = 1;
    TextView txtTitle, txtName1,txtEmail, txtPassword, txtPromocode, txtAgreeing, txtTerms;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    private static final int REQUEST_CODE_TOKEN_AUTH = 1;
    private static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 100;
    private CallbackManager callbackManager;
    private ImageView btnFacebook; //btnFacebook
    private Button btnGPlus;
        //LoginButton btnFacebook;
    private Button btnSignUp;
    private SignUpServiceHelper signUpServiceHelper;
    private EditText edtUserName, edtPassword, name, city,edtPromocode;
    private ProgressDialogHelper progressDialogHelper;
    private TextView txtSignUp, termsView, titleView, agree;
    private static GoogleApiClient mGoogleApiClient;
    private int REQUEST_SOLVE_CONNECTION = 999;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private boolean mResolvingError = false;
    private SharedPreferences mSharedPreferences;
    private View mDecorView;
    private int mSelectedLoginMode = 0;
    public View view;
    public View viewResult;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String PROFILE_USER_ID = "USER_ID";
    public static final String PROFILE_DISPLAY_NAME = "PROFILE_DISPLAY_NAME";
    public static final String PROFILE_USER_EMAIL = "USER_PROFILE_EMAIL";
    public static final String PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_signup, container, false);

        checkPlayServices();

        viewResult = initializeViews(view);

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getActivity());
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    "world.of.fun",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        generateFacebookKeys();
        FacebookSdk.sdkInitialize(getActivity());
        initFacebook();

        signUpServiceHelper = new SignUpServiceHelper(getActivity());
        signUpServiceHelper.setSignUpServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());

        return viewResult;

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
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
                                                jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_up");
                                                jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, email);
                                                jsonObject.put(FindAFunConstants.PARAMS_USER_PASSWORD, FindAFunConstants.DEFAULT_PASSWORD);
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

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            if (view == btnFacebook) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
                PreferenceStorage.saveLoginMode(getActivity(), FindAFunConstants.FACEBOOK);
                mSelectedLoginMode = FindAFunConstants.FACEBOOK;
            } else if (view == btnSignUp) {

                if (validateFields()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        signUpClick = 2;
                        jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_up");
                        jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, edtUserName.getText().toString());
                        jsonObject.put("user_password", edtPassword.getText().toString());
                        //jsonObject.put(FindAFunConstants.PARAMS_USER_CITY, edtPassword.getText().toString());
                        jsonObject.put("user_name", name.getText().toString());
                        jsonObject.put("ex_promocode", edtPromocode.getText().toString());
                        jsonObject.put(FindAFunConstants.PARAMS_SIGN_UP_TYPE, "2");
                        mSelectedLoginMode = FindAFunConstants.NORMAL_SIGNUP;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    signUpServiceHelper.makeSignUpServiceCall(jsonObject.toString());
                    PreferenceStorage.saveLoginMode(getActivity(), 2);
                }
            } else if (view == txtSignUp) {
                Intent signUpIntent = new Intent(getActivity(), SignupFragment.class);
                startActivity(signUpIntent);
            } else if (view == btnGPlus) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PreferenceStorage.saveLoginMode(getActivity(), FindAFunConstants.GOOGLE_PLUS);
//                    mSelectedLoginMode = FindAFunConstants.FACEBOOK;
                    mSelectedLoginMode = FindAFunConstants.GOOGLE_PLUS;
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                       // Log.d(TAG, "initiate google plus sign up");
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
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), "No Network connection available");
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


    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {

                    token = GoogleAuthUtil.getToken(
                            getActivity(),
                            Plus.AccountApi.getAccountName(mGoogleApiClient), "oauth2:"
                                    +
                                    Scopes.PLUS_LOGIN);

                    Log.e("Access Token-->", token);


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

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "sign_up");
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mResolvingError = false;
            if (resultCode == getActivity().RESULT_OK) {
                // If we have a successful result, we will want to be able to
                // resolve any further errors, so turn on resolution with our
                // flag.
                //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                //handleSignInResult(result);

                mSignInClicked = true;
                // If we have a successful result, lets call connect() again. If
                // there are any more errors to resolve we'll get our
                // onConnectionFailed, but if not, we'll get onConnected.
                mGoogleApiClient.connect();

            } else if (resultCode != getActivity().RESULT_OK) {
                // If we've got an error we can't resolve, we're no
                // longer in the midst of signing in, so we can stop
                // the progress spinner.
                progressDialogHelper.hideProgressDialog();
            }

        } else if (requestCode == REQUEST_CODE_TOKEN_AUTH) {
            if (resultCode == getActivity().RESULT_OK) {
            }
        }

        //if (requestCode == RC_SIGN_IN) {}
    }

    private boolean validateFields() {
        if (!FindAFunValidator.checkNullString(this.name.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), this.getResources().getString(R.string.enter_user_name));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.edtUserName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), this.getResources().getString(R.string.email_empty_str));
            return false;
        } else if (!FindAFunValidator.isEmailValid(this.edtUserName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), this.getResources().getString(R.string.enter_valid_email));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.edtPassword.getText().toString())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), this.getResources().getString(R.string.enter_password));
            return false;
        } else if (!FindAFunValidator.withinPermittedLength(this.edtPassword.getText().toString())) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), "Password length should be greater than 6 characters");
            return false;
        } else {
            return true;
        }
    }


    private void generateFacebookKeys() {
        Log.d(TAG, "Generating facebook has keys");
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
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
    private View initializeViews(View view) {
        btnFacebook = (ImageView) view.findViewById(R.id.frag_signup_fb);
        btnFacebook.setOnClickListener(this);
        btnGPlus = (Button) view.findViewById(R.id.frag_signup_google);
        btnGPlus.setOnClickListener(this);
        btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(this);
        edtUserName = (EditText) view.findViewById(R.id.editText_email);
        edtPassword = (EditText) view.findViewById(R.id.editText_password);
        edtPromocode= (EditText) view.findViewById(R.id.editText_promocode);
        name = (EditText) view.findViewById(R.id.editText_name);
        titleView =(TextView) view.findViewById(R.id.create_title);
        agree = (TextView) view.findViewById(R.id.textView_agreeing_up);
        termsView = (TextView) view.findViewById(R.id.textView_terms);
        SpannableString string = new SpannableString("Terms and conditions");
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        termsView.setText(string);
        termsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TermsAndConditionsActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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


            if (signUpClick == 2) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Registration Successful");


                alertDialogBuilder.setMessage("Activation Link sent to your email. Activate it and perform Sign In");
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
//                                Intent intent = new Intent(getActivity(), LoginNew.class);
//                                startActivity(intent);
//                                getActivity().finish();

                                SharedPreferences sharedPreferences =
                                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                                sharedPreferences.edit().clear().commit();
                                TwitterUtil.getInstance().resetTwitterRequestToken();

                                Intent homeIntent = new Intent(getActivity(), SplashScreenActivity.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                getActivity().finish();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Registration Successful");


                alertDialogBuilder.setMessage("Please Sign In");
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
//                                Intent intent = new Intent(getActivity(), LoginNew.class);
//                                startActivity(intent);
//                                getActivity().finish();

                                SharedPreferences sharedPreferences =
                                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                                sharedPreferences.edit().clear().commit();
                                TwitterUtil.getInstance().resetTwitterRequestToken();

                                Intent homeIntent = new Intent(getActivity(), SplashScreenActivity.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                getActivity().finish();

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }


            /*Intent intent = new Intent(this, SelectCityActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();*/
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
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            //alertDialogBuilder.setTitle("Registration Successful");
                            alertDialogBuilder.setMessage(msg);
                            alertDialogBuilder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
//                                            Intent intent = new Intent(getActivity(), LoginNewActivity.class);
//                                            startActivity(intent);
//                                            getActivity().finish();

                                            SharedPreferences sharedPreferences =
                                                    PreferenceManager.getDefaultSharedPreferences(getActivity());
                                            sharedPreferences.edit().clear().commit();
                                            TwitterUtil.getInstance().resetTwitterRequestToken();

                                            Intent homeIntent = new Intent(getActivity(), SplashScreenActivity.class);
                                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(homeIntent);
                                            getActivity().finish();

                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            Log.d(TAG, "Show error dialog");
                            AlertDialogHelper.showSimpleAlertDialog(getActivity(), msg);
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
    public void onSignUpError(String error) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
