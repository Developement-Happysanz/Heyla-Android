package com.findafun.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.findafun.R;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.IForgotPasswordServiceListener;

import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Data Crawl 6 on 22-04-2016.
 */
public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, IForgotPasswordServiceListener, DialogClickListener {
    private static final String TAG = ForgotPasswordActivity.class.getName();
    private Button btnReset;
    private EditText edtEmailId, edtLastRememberPassword, edtNewPassword, edtRetypePassword;
    private ProgressDialogHelper progressDialogHelper;
    private SignUpServiceHelper signUpServiceHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initializeViews();
        signUpServiceHelper = new SignUpServiceHelper(this);
        signUpServiceHelper.setForgotPasswordServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    // Initialize Views
    private void initializeViews() {
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        edtEmailId = (EditText) findViewById(R.id.editText_EmailId);
        edtLastRememberPassword = (EditText) findViewById(R.id.editText_LastRemPass);
        edtNewPassword = (EditText) findViewById(R.id.editText_Newpassword);
        edtRetypePassword = (EditText) findViewById(R.id.editText_Retypepassword);
    }

    @Override
    public void onClick(View view) {
        if (CommonUtils.isNetworkAvailable(this)) {
            if (view == btnReset) {
                if (validateFields()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "forgot_password");
                        jsonObject.put(FindAFunConstants.PARAMS_USER_NAME, edtEmailId.getText().toString());
                        jsonObject.put("user_password", edtLastRememberPassword.getText().toString());
                        jsonObject.put("new_password", edtNewPassword.getText().toString());
                        jsonObject.put("retype_password", edtRetypePassword.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    signUpServiceHelper.makeForgotPasswordServiceCall(jsonObject.toString());
                    //PreferenceStorage.saveLoginMode(this, 2);
                }
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private boolean validateFields() {
        if (!FindAFunValidator.checkNullString(this.edtEmailId.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.email_empty_str));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.edtLastRememberPassword.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.enter_password));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.edtNewPassword.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.enter_password));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.edtRetypePassword.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.enter_password));
            return false;
        }else if (!this.edtNewPassword.getText().toString().trim().contentEquals(this.edtRetypePassword.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.password_mismatch));
            return false;
        }else {
            return true;
        }
    }

    private boolean validateForgotPasswordResponse(JSONObject response) {
        boolean forgotPasswordsuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(FindAFunConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        forgotPasswordsuccess = false;
                        if (status.equalsIgnoreCase("notRegistered")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            //alertDialogBuilder.setTitle("Registration Successful");
                            alertDialogBuilder.setMessage(msg);
                            alertDialogBuilder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginNewActivity.class);
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
                        forgotPasswordsuccess = true;

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return forgotPasswordsuccess;
    }

    @Override
    public void onForgotPassword(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateForgotPasswordResponse(response)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Password reset successful");
            alertDialogBuilder.setMessage("Password reset successfully and perform Sign In");
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginNewActivity.class);
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
    public void onForgotPasswordError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
