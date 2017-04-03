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

import com.inspirenetz.app.proapp.dictionary.UserType;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.UserAuthenticationResponse;
import com.inspirenetz.app.proapp.resources.UserInfoResource;
import com.inspirenetz.app.proapp.service.CustomerService;


/**
 * Created by Raju on 18-Aug-16.
 */
public class LoginActivity extends Activity {

    // Declare context
    private Context context;

    // Declare mobile number;
    private EditText txtCusMobile;

    // Declare password
    private EditText txtCusPassword;

    // Declare button reset login
    private Button btnResetLogin;

    // Declare button sign in
    private Button btnSignIn;

    // Declare new customer register label
    private TextView lblNewCusRegister;

    // Declare forgot password text view
    private TextView lblForgotPassword;

    // keep mobile no in string
    private String mobileNo = "";

    // Keep password in string
    private String password = "";

    // Declare dialog
    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    //Create an instance of general method
    final GeneralMethods generalMethods = new GeneralMethods();

    private UserAuthenticationResponse userAuthenticationResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Set the orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Make full screen app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create session manager object
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        // Check the login
        boolean loggedIn = sessionManager.isLoggedIn();

        // If loggedIn , then we need to show home page
        if ( loggedIn ) {

            UserInfoResource userInfoResource = sessionManager.getUserInfo();

            if(userInfoResource!=null){

                if(userInfoResource.getUsrUserType()== UserType.CUSTOMER){

                    showCustomerHomePage();

                }

                Log.d("logged in ", "logged in");
            }
        }

        setContentView(R.layout.activity_login);

        /*Bundle bundle = getIntent().getExtras();
        mobileNo = bundle.getString("mobileNO");*/

        initialize();
    }

    // Runnable instance for cash back transact method
    final Runnable rCheckCustomerInfo = new Runnable() {
        @Override
        public void run() {

            updateCustomerAuthenticateInfo();
        }
    };


    /**
     * Method to initialize the fields
     */
    protected void initialize(){

        // Get context
        context = LoginActivity.this;

        // Get mobile number
        txtCusMobile = (EditText)findViewById(R.id.txtCusMobile);
        //txtCusMobile.setText(mobileNo);

        // Get password
        txtCusPassword = (EditText)findViewById(R.id.txtCusPassword);

        // Get start button
        btnSignIn = (Button)findViewById(R.id.btnSignIn);

        // Get reset button
        btnResetLogin = (Button)findViewById(R.id.btnResetLogin);

        // Get new customer register label
        lblNewCusRegister = (TextView)findViewById(R.id.lblNewCusRegister);

        // Get forgot password label
        lblForgotPassword = (TextView)findViewById(R.id.lblForgotPassword);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCustomerInfo();
            }
        });


        lblForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                resetLogin();
            }
        });

        // Click on sign in done button
        txtCusPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){

                    // call the method to check the customer info
                    checkCustomerInfo();
                }
                return false;
            }
        });

        lblNewCusRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCustomerRegisterPage();
            }
        });
    }

    /**
     * Function to show start the home activity
     */
    public void showCustomerHomePage(){

        Intent intent = new Intent(this,CustomerHomeActivity.class);

        startActivity(intent);

        finish();
    }

    /**
     * Function to show start the home activity
     */
    public void showCustomerRegisterPage(){

        /*if(txtCusMobile.getText().toString().equals("")){

            txtCusMobile.setError(getString(R.string.err_customer_mobile));

            return;
        }*/
        mobileNo = txtCusMobile.getText().toString().trim();
        Intent intent = new Intent(this,RegisterActivity.class);

        intent.putExtra("mobileNO", mobileNo);

        startActivity(intent);

        finish();
    }

    /**
     * Function to authenticate the customer
     * using mobile no and password
     */
    public void checkCustomerInfo(){

        if(validateLoginDetails()) {

            //Create a custom progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Login", "Authenticating Customer...");

            // Show dialog
            dlgProgress.show();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread thread = new Thread() {

                public void run() {

                    try {

                        // Create an object for CustomerService class
                        CustomerService customerService = new CustomerService();

                        // Hold the response
                        userAuthenticationResponse = customerService.authenticateUser(mobileNo, password);

                    } catch (Exception e) {

                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    // Post to the mUpdateCustomerSearch runnable
                    mHandler.post(rCheckCustomerInfo);

                }
            };
            thread.start();
        }
    }

    /**
     * Function to validate the customer login details
     * @return true if the field is not empty or return false
     */
    public boolean validateLoginDetails(){

        boolean isValid = true;

        mobileNo = txtCusMobile.getText().toString().trim();

        password = txtCusPassword.getText().toString().trim();

        if(mobileNo.equals("")){

            txtCusMobile.setError(getString(R.string.err_customer_mobile));

            isValid = false;

        }

        if(password.equals("")){

            txtCusPassword.setError(getString(R.string.err_customer_password));

            isValid = false;

        }

        if(!isValid) return false;

        return isValid;
    }


    /*
    * Function to Change the View to ResetLogin View
    *
    * */
    public void resetLogin(){

        if(txtCusMobile.getText().toString().equals("")){

            txtCusMobile.setError(getString(R.string.err_customer_mobile));

            return;
        }
        mobileNo = txtCusMobile.getText().toString().trim();
        Intent intent = new Intent(this,ResetLoginActivity.class);

        intent.putExtra("mobileNO", mobileNo);

        startActivity(intent);

        finish();
    }

    /**
     * Function to update the response
     * after called the api from the server
     *
     */
    public void updateCustomerAuthenticateInfo(){

        // Dismiss the dialog
        dlgProgress.dismiss();

        if (userAuthenticationResponse.getStatus().equals("success")) {

            // Create the session manager object
            SessionManager session = new SessionManager(context);

            // Check the login
            session.createLoginSession(mobileNo, password, userAuthenticationResponse.getData());

            // If authenticated, then we need to show the message
            generalMethods.showToastMessage(context, "Logging in...");

            SessionManager sessionManager = new SessionManager(context);

            UserInfoResource userInfoResource = sessionManager.getUserInfo();

            if (userInfoResource.getUsrUserType() == UserType.CUSTOMER) {

                showCustomerHomePage();

            } else {

                generalMethods.showToastMessage(context, "Please check the user id");
            }

        } else{

            // Show the error message
            generalMethods.showToastMessage(context, userAuthenticationResponse.getErrorcode());

        }

    }

}
