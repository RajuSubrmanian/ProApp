package com.inspirenetz.app.proapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.OneTimePasswordResponse;
import com.inspirenetz.app.proapp.service.OTPService;


/**
 * Created by Raju on 21-Aug-16.
 */
public class OTPValidateActivity extends Activity {

    // Declare context
    private Context context;

    // Declare otp filed
    private EditText txtOTP;

    // Declare button resend
    private Button btnResend;

    // Declare button confirm
    private Button btnConfirm;

    // Keep mobile no in string
    private String mobileNo = "";

    // Declare dialog
    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    //Create an instance of general method
    final GeneralMethods generalMethods = new GeneralMethods();

    private OneTimePasswordResponse customerRegisterOTPResponse;

    private OneTimePasswordResponse validateRegisterOTPResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Set the orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Make full screen app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create session manager object
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_otp_validate);

        Bundle bundle = getIntent().getExtras();
        mobileNo = bundle.getString("mobileNO");

        context = OTPValidateActivity.this;

        initialize();

        generateRegisterOTP();
    }

    /**
     * Method to initialize all fields
     */
    public void initialize(){

        // Get otp
        txtOTP = (EditText)findViewById(R.id.txtOTP);

        // Get button resend
        btnResend = (Button) findViewById(R.id.btnResend);

        // Get button confirm
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generateRegisterOTP();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateRegisterOtp();
            }
        });

        // Start click register button
        txtOTP.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // call the method to validate otp
                    validateRegisterOtp();

                }
                return false;
            }
        });

    }

    /**
     * Method to redirect to login activity
     */
    protected void showLoginPage(){

        Intent intent=new Intent(this,LoginActivity.class);

        startActivity(intent);

        finish();
    }


    // Runnable instance for send the OTP method
    final Runnable mSendCustomerRegisterOTP = new Runnable() {
        @Override
        public void run() {
            updateOTPStatus();
        }
    };

    // Runnable instance for send the OTP method
    final Runnable mValidateRegisterOTP = new Runnable() {
        @Override
        public void run() {
            updateValidateRegisterOTPStatus();
        }
    };

    /**
     * Method to generate customer register otp
     * and send to customer
     */
    public void generateRegisterOTP(){

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread thread = new Thread(){

                public void run(){

                    try{

                        // Create service object for OTPService class
                        OTPService otpService = new OTPService();

                        // Hold the response
                        customerRegisterOTPResponse = otpService.generateOTP(context, mobileNo);


                    }catch (Exception e){

                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    // Post to the mUpdateCustomerSearch runnable
                    mHandler.post(mSendCustomerRegisterOTP);

                }
            };
            thread.start();
    }

    /**
     * Method to display OTP message
     */
    public void updateOTPStatus(){

        if(customerRegisterOTPResponse.getStatus().equals("success")){

            //Show toast Message
            generalMethods.showToastMessage(context,"OTP have been sent");

        }else{

            //show error in toast
            generalMethods.showToastMessage(context,customerRegisterOTPResponse.getErrorcode());
        }

    }


    /**
     * Method to validate customer register otp
     * and confirm the registration
     */
    public void validateRegisterOtp(){

        final String otpCOde = txtOTP.getText().toString().trim();

        if(otpCOde.equals("")){

            txtOTP.setError(getString(R.string.err_otp_validate));

        }else {

            // get the Reference to the custom  progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Registration Process", "Completing...");

            // Show the dialog
            dlgProgress.show();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread thread = new Thread() {

                public void run() {

                    try {

                        // Create service object for OTPService class
                        OTPService otpService = new OTPService();

                        // Hold the response
                        validateRegisterOTPResponse = otpService.validateRegisterOTP(context, mobileNo, otpCOde);


                    } catch (Exception e) {

                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    // Post to the mUpdateCustomerSearch runnable
                    mHandler.post(mValidateRegisterOTP);

                }
            };
            thread.start();
        }
    }

    /**
     * Method to update the otp validating status
     * after call the api from the server
     */
    public void updateValidateRegisterOTPStatus(){

        dlgProgress.dismiss();

        if(validateRegisterOTPResponse.getStatus().equals("success")){

            //Show toast Message
            generalMethods.showToastMessage(context,"Registration Success");

            showLoginPage();

        }else{

            //show error in toast
            generalMethods.showToastMessage(context,validateRegisterOTPResponse.getErrorcode());
        }

    }

}
