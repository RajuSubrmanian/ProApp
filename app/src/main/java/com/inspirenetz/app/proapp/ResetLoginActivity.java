package com.inspirenetz.app.proapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.resources.ForgotPasswordResponse;
import com.inspirenetz.app.proapp.resources.OneTimePasswordResponse;
import com.inspirenetz.app.proapp.service.ForgotPasswordService;
import com.inspirenetz.app.proapp.service.OTPService;


/**
 * Created by Mishab on 19-08-2016.
 */
public class ResetLoginActivity extends Activity {

    // Keep Mobile No. in String
    private String mobileNo = "";

    private Button btnProceed ;

    private TextView lblOTPResend ;

    // Declare mobile number;
    private TextView lblCusMobile;

    private TextView lblChange;

    private EditText txtNewPassword;

    private EditText txtRENewPassword;

    private EditText txtResetLoginOTP;

    // Declare new customer register label
    private TextView lblNewCusRegister;

    // Declare dialog
    private Dialog dlgProgress;

    // Declare context
    private Context context;

    final Handler mHandler = new Handler();

    //Create an instance of general method
    final GeneralMethods generalMethods = new GeneralMethods();

    private OneTimePasswordResponse passwordResetOTPResponse;

    private ForgotPasswordResponse forgotPasswordResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Set the orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Make full screen app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_reset_login);

        Bundle bundle = getIntent().getExtras();
        mobileNo = bundle.getString("mobileNO");

        context =  ResetLoginActivity.this;

        initialize();

        generateResetOTP();
    }

    /*
    * Method to Initialize the Fields
    * */

    public void initialize(){

        lblCusMobile = (TextView)findViewById(R.id.lblResetLoginMobile);

        lblCusMobile.setText(mobileNo);

        lblChange = (TextView)findViewById(R.id.lblResetLoginChange);

        lblChange.setText("CHANGE");

        Linkify.addLinks(lblChange, Linkify.ALL);

        lblChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWelcomePage();
            }
        });

        txtNewPassword =(EditText)findViewById(R.id.txtResetLoginNewPassword);

        txtRENewPassword = (EditText)findViewById(R.id.txtResetLoginREPassword);

        txtResetLoginOTP = (EditText)findViewById(R.id.txtResetLoginOTP);

        lblOTPResend = (TextView) findViewById(R.id.lblOTPResend);

        lblOTPResend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                generateResetOTP();
            }
        });

        btnProceed = (Button)findViewById(R.id.btnResetLoginProceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetLogin();
            }
        });

        // Get new customer register label
        lblNewCusRegister = (TextView)findViewById(R.id.lblNewCusRegister);

        lblNewCusRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCustomerRegisterPage();
            }
        });

        // Click on reset login in done button
        txtResetLoginOTP.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){

                    // call the method to reset the customer password
                    resetLogin();
                }
                return false;
            }
        });
    }

    /**
     * Function to show start the home activity
     */
    public void showCustomerRegisterPage(){

        Intent intent = new Intent(this,RegisterActivity.class);

        intent.putExtra("mobileNO", mobileNo);

        startActivity(intent);

        finish();
    }


    // Runnable instance for send the OTP method
    final Runnable mSendPasswordResetOTP = new Runnable() {
        @Override
        public void run() {
            updateOTPStatus();
        }
    };

    /*
    * Method to Generate ResetLogin OTP
    * */
    public void generateResetOTP(){

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread thread = new Thread(){

            public void run(){

                try{

                    // Create service object for OTPService class
                    OTPService otpService = new OTPService();

                    // Hold the response
                    passwordResetOTPResponse = otpService.generateResetOTP(context, mobileNo);


                }catch (Exception e){

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                }

                // Post to the mUpdateCustomerSearch runnable
                mHandler.post(mSendPasswordResetOTP);

            }
        };
        thread.start();
    }

    /**
     * Method to display OTP message
     */
    public void updateOTPStatus(){

        if(passwordResetOTPResponse.getStatus().equals("success")){
            //Show toast Message
            generalMethods.showToastMessage(context,"OTP have been sent");
        }else{
            //show error in toast
            generalMethods.showToastMessage(context,passwordResetOTPResponse.getErrorcode());
        }

    }

    //Runnable instance for updateCustomerRegistration method
    final Runnable mUpdateResetLogin = new Runnable() {

        @Override
        public void run() {
            updateResetLogin();
        }

    };


    public boolean validatePassword (){

        // Initially set the isValid flag to true
        boolean isValid = true;

        String password = txtNewPassword.getText().toString().trim();

        String confirmPassword = txtRENewPassword.getText().toString().trim();

        String otp = txtResetLoginOTP.getText().toString();

        if(password.equals("")){

            txtNewPassword.setError(getString(R.string.err_reset_login_password));

            isValid = false;

        }else if(confirmPassword.equals("")){

            txtRENewPassword.setError(getString(R.string.err_reset_login_confirm_password));

            isValid = false;

        }else if(!password.equals(confirmPassword)){

            txtRENewPassword.setError(getString(R.string.err_resetLogin_passwordMatch));

            isValid = false;

        }else if (otp.equals("")){

            //txtResetLoginOTP.setError(getString(R.string.err_reset_login_otp));

            generalMethods.showToastMessage(context, getString(R.string.err_reset_login_otp));

            isValid = false;
        }

        if (!isValid) return false;

      return isValid;

    }


    public void resetLogin(){

        if(validatePassword()) {

            final String newPassword = txtNewPassword.getText().toString().trim();
            final String otp = txtResetLoginOTP.getText().toString().trim();


            // get the Reference to the custom  progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Updating", "In Progress");

            // Show the dialog
            dlgProgress.show();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread t = new Thread() {

                public void run() {

                    try {
                        ForgotPasswordService passwordService = new ForgotPasswordService();

                        forgotPasswordResponse = passwordService.resetPassword(context, mobileNo, newPassword, otp);

                    } catch (Exception e) {
                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    //Post to the mUpdateCustomerRegistration runnable
                    mHandler.post(mUpdateResetLogin);
                }
            };
            t.start();
        }
    }

    public void updateResetLogin(){
        //Dismiss dialog
        dlgProgress.dismiss();

        if(forgotPasswordResponse.getStatus().equals("success")){

            //Log the error
            //Log.d("Login Reset", "Password Changed Successfully");

            //show toast message
            generalMethods.showToastMessage(context,"Password have changed");

            showLoginPage();

        }else if (forgotPasswordResponse.getStatus().equals("failed")){

            if(forgotPasswordResponse.getErrorcode().equals("ERR_INVALID_OTP")){

                //show message in toast
                generalMethods.showToastMessage(context, "Invalid OTP");

                return;

            }else {

                //show error in toast
                generalMethods.showToastMessage(context,forgotPasswordResponse.getErrorcode());

            }

        }

    }

    public void showLoginPage(){
        Intent intent = new Intent(this,LoginActivity.class);

        startActivity(intent);

        finish();
    }

    /**
     * Method to show the welcome page
     * when click on the change link
     */
    public  void showWelcomePage(){

        Intent intent = new Intent(this,MainActivity.class);

        startActivity(intent);

        finish();
    }



}
