package com.findafun.activity.heyla;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.activity.FirstTimePreference;
import com.findafun.activity.LandingActivity;
import com.findafun.activity.LoginNew;
import com.findafun.activity.LoginNewActivity;
import com.findafun.activity.SelectCityActivity;
import com.findafun.activity.SelectPreferenceActivity;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;
import com.findafun.utils.PermissionUtil;
import com.findafun.utils.PreferenceStorage;

/**
 * Created by Nandha on 11-03-2017.
 */

public class LoginDashboardActivity extends AppCompatActivity implements View.OnClickListener, DialogClickListener {

    private static final String TAG = LoginDashboardActivity.class.getName();
    TextView txtGFLogin, txtOR;
    private ImageView imgLogin, imgCreateAccount;
    private ImageView btnFacebook, btnGPlus, txtGuestLogin;
    private ImageView btnLogin, btnCreateAccount;
    private static final int REQUEST_PERMISSION_All = 111;
    private static String[] PERMISSIONS_ALL = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private SignUpServiceHelper signUpServiceHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dashboard);

        if (PreferenceStorage.getUserId(getApplicationContext()) != null && FindAFunValidator.checkNullString(PreferenceStorage.getUserId(getApplicationContext()))) {
            String city = PreferenceStorage.getUserCity(getApplicationContext());
            boolean haspreferences = PreferenceStorage.isPreferencesPresent(getApplicationContext());
            if (FindAFunValidator.checkNullString(city) && haspreferences) {
                Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                startActivity(intent);
                this.finish();
            } else if (!FindAFunValidator.checkNullString(city)) {
                Log.d(TAG, "No city yet, show city activity");
                Intent intent = new Intent(getApplicationContext(), SelectCityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();

            } else if (!haspreferences) {
                Log.d(TAG, "No preferences, so launch preferences activity");
                Intent intent = new Intent(getApplicationContext(), SelectPreferenceActivity.class);
                intent.putExtra("selectedCity", city);
                startActivity(intent);
                this.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                this.finish();
            }
        } else {

            btnFacebook = (ImageView) findViewById(R.id.frag_login_fb);
            btnGPlus = (ImageView) findViewById(R.id.frag_login_gplus);
            btnLogin = (ImageView) findViewById(R.id.btn_login);
            btnCreateAccount = (ImageView) findViewById(R.id.btn_create_new_account);
            txtGuestLogin = (ImageView) findViewById(R.id.btn_guest_login);
            txtGFLogin = (TextView)findViewById(R.id.txt_gflogin);
            txtOR = (TextView)findViewById(R.id.txt_or);


            Typeface myFont = Typeface.createFromAsset(getAssets(),"Roboto.ttf");
            txtGFLogin.setTypeface(myFont);
            txtOR.setTypeface(myFont);
            btnLogin.setOnClickListener(this);
            btnCreateAccount.setOnClickListener(this);
            txtGuestLogin.setOnClickListener(this);
            btnFacebook.setOnClickListener(this);
            btnGPlus.setOnClickListener(this);

            FirstTimePreference prefFirstTime = new FirstTimePreference(getApplicationContext());

            if (prefFirstTime.runTheFirstTime("FirstTimePermit")) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    requestAllPermissions();
                }
            }
        }
    }

    private void requestAllPermissions() {

        boolean requestPermission = PermissionUtil.requestAllPermissions(this);

        if (requestPermission == true) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.

            ActivityCompat
                    .requestPermissions(LoginDashboardActivity.this, PERMISSIONS_ALL,
                            REQUEST_PERMISSION_All);
        } else {

            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, REQUEST_PERMISSION_All);
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.isNetworkAvailable(this)) {
            if (v == btnLogin) {
                Intent signUpIntent = new Intent(getApplicationContext(), LoginNewActivity.class);
                signUpIntent.putExtra("Value", "login");
                startActivity(signUpIntent);

            } else if (v == btnCreateAccount) {

                Intent signUpIntent = new Intent(getApplicationContext(), LoginNewActivity.class);
                signUpIntent.putExtra("Value", "signup");
                startActivity(signUpIntent);

            } else if (v == txtGuestLogin) {

                Intent signInIntent = new Intent(getApplicationContext(), LoginSocialActivity.class);
                signInIntent.putExtra("LoginType", "1");
                startActivity(signInIntent);
            } else if (v == btnFacebook) {

                Intent signInIntent = new Intent(getApplicationContext(), LoginSocialActivity.class);
                signInIntent.putExtra("LoginType", "2");
                startActivity(signInIntent);
            } else if (v == btnGPlus) {

                Intent signInIntent = new Intent(getApplicationContext(), LoginSocialActivity.class);
                signInIntent.putExtra("LoginType", "3");
                startActivity(signInIntent);
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
