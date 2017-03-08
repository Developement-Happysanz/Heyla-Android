package com.findafun.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.findafun.R;
import com.findafun.activity.heyla.Login;
import com.findafun.activity.heyla.Signup;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunValidator;
import com.findafun.utils.PermissionUtil;
import com.findafun.utils.PreferenceStorage;

/**
 * Created by Nandha on 20-02-2017.
 */

public class LoginNew extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginNew.class.getName();
    private ImageView imgLogin, imgCreateAccount;
    private ImageView btnFacebook;
    private ImageView btnLogin, btnCreateAccount;
    private static final int REQUEST_PERMISSION_All = 111;
    private static String[] PERMISSIONS_ALL = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_heyla);

        if (PreferenceStorage.getUserId(getApplicationContext()) != null && FindAFunValidator.checkNullString(PreferenceStorage.getUserId(getApplicationContext()))) {
            String city = PreferenceStorage.getUserCity(getApplicationContext());
            boolean haspreferences = PreferenceStorage.isPreferencesPresent(getApplicationContext());
            if( FindAFunValidator.checkNullString(city) && haspreferences) {
                Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                startActivity(intent);
                this.finish();
            }else if(!FindAFunValidator.checkNullString(city)){
                Log.d(TAG,"No city yet, show city activity");
                Intent intent = new Intent(getApplicationContext(), SelectCityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();

            } else if(!haspreferences){
                Log.d(TAG,"No preferences, so launch preferences activity");
                Intent intent = new Intent(getApplicationContext(), SelectPreferenceActivity.class);
                intent.putExtra("selectedCity", city);
                startActivity(intent);
                this.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                this.finish();
            }
        } else {

            btnFacebook = (ImageView) findViewById(R.id.frag_login_fb);
            btnLogin = (ImageView) findViewById(R.id.btn_login);
            btnCreateAccount = (ImageView) findViewById(R.id.btn_create_new_account);

            btnLogin.setOnClickListener(this);
            btnCreateAccount.setOnClickListener(this);

            FirstTimePreference prefFirstTime = new FirstTimePreference(getApplicationContext());

            if (prefFirstTime.runTheFirstTime("FirstTimePermit")) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    requestAllPermissions();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

            if (v == btnLogin) {
                Intent signUpIntent = new Intent(getApplicationContext(), LoginNewActivity.class);
                signUpIntent.putExtra("Value", "login");
                startActivity(signUpIntent);

            } else if (v == btnCreateAccount){

                Intent signUpIntent = new Intent(getApplicationContext(), LoginNewActivity.class);
                signUpIntent.putExtra("Value", "signup");
                startActivity(signUpIntent);
            }
    }

    private void requestAllPermissions() {

        boolean requestPermission = PermissionUtil.requestAllPermissions(this);

        if (requestPermission == true) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.

            ActivityCompat
                    .requestPermissions(LoginNew.this, PERMISSIONS_ALL,
                            REQUEST_PERMISSION_All);
        } else {

            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, REQUEST_PERMISSION_All);
        }
    }
}
