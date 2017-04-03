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
import android.widget.ImageView;
import android.widget.TextView;

import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.CustomerRegisterResponse;
import com.inspirenetz.app.proapp.resources.CustomerResource;
import com.inspirenetz.app.proapp.resources.CustomerSaveResponse;
import com.inspirenetz.app.proapp.service.CustomerService;


/**
 * Created by Raju on 18-Aug-16.
 */
public class RegisterActivity extends Activity {


    // Declare context
    private Context context;

    // Declare mobile number
    private TextView lblMobileNo;

    // Declare change label
    private TextView lblChange;

    // Declare  customer mobile no
    private EditText txtCusMobileNo;

    // Declare  customer password
    private EditText txtCusPassword;

    // Declare customer Name
    private EditText txtCusName;

    // Declare customer email
    private EditText txtCusEmail;

    // Declare back arrow image
    private ImageView imgBackArrow;

    // Declare register button
    private Button btnRegister;

    // Keep mobile no in string
    private String mobileNo = "";

    // Declare dialog
    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    private CustomerSaveResponse customerSaveResponse;

    private CustomerRegisterResponse customerRegisterResponse;

    //Create an instance of general method
    final GeneralMethods generalMethods = new GeneralMethods();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Set the orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Make full screen app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context =  RegisterActivity.this;

        // Create session manager object
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_register);

        Bundle bundle = getIntent().getExtras();
        mobileNo = bundle.getString("mobileNO");

        initialize();
    }

    //Runnable instance for updateCustomerRegistration method
    final Runnable mUpdateCustomerRegistration = new Runnable() {

        @Override
        public void run() {
            updateCustomerRegistration();
        }

    };


    /**
     * Method to initialize all fields
     *
     */
    protected void initialize(){

        // Get Mobile no
        lblMobileNo = (TextView)findViewById(R.id.lblMobileNo);

        // Get mobile no in edit text filed
        txtCusMobileNo = (EditText)findViewById(R.id.txtCusMobileNo);

        // Get password
        txtCusPassword = (EditText) findViewById(R.id.txtCusPassword);

        // Get Name
        txtCusName = (EditText) findViewById(R.id.txtCusName);

        // Get email
        txtCusEmail = (EditText) findViewById(R.id.txtCusEmail);

        // Get register button
        btnRegister = (Button)findViewById(R.id.btnRegister);

        // Get back arrow button
        imgBackArrow = (ImageView)findViewById(R.id.imgBackArrow);

        // Get Mobile no
        lblChange = (TextView)findViewById(R.id.lblChange);
        lblChange.setText("CHANGE");
        Linkify.addLinks(lblChange, Linkify.ALL);

        // Set mobile in the text view field
        txtCusMobileNo.setText(mobileNo);

        lblChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWelcomePage();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                posCustomerRegister();

            }
        });

        // Start click register button
        txtCusEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // call the method to check crustomer
                    posCustomerRegister();

                }
                return false;
            }
        });

        // Start click register button
        txtCusPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // call the method to check crustomer
                    posCustomerRegister();

                }
                return false;
            }
        });

        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoginPage();
            }
        });
    }

    /**
     * Method to show the welcome page
     * when click on the change link
     */
    public void showWelcomePage(){

        Intent intent = new Intent(this,MainActivity.class);

        startActivity(intent);

        finish();

    }

    /**
     * Method to show the welcome page
     * when click on the change link
     */
    public void showLoginPage(){

        Intent intent = new Intent(this,LoginActivity.class);

        startActivity(intent);

        finish();

    }

    /**
     * Method to redirect to otp validate activity
     */
    protected void showOtpValidatePage(){

        Intent intent=new Intent(this,OTPValidateActivity.class);

        intent.putExtra("mobileNO", mobileNo);

        startActivity(intent);

        finish();
    }

    /**
     * Method to post the customer register values
     *
     */
    public void posCustomerRegister(){

        //validate customer information
        if(validateCustomerProfile()) {

            final String name = txtCusName.getText().toString().trim();
            final String password = txtCusPassword.getText().toString().trim();
            final String email = txtCusEmail.getText().toString().trim();
            final String mobile = txtCusMobileNo.getText().toString().trim();

            final CustomerResource customerResource=createCustomerObject();

            // get the Reference to the custom  progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Customer Register", "In Progress");

            // Show the dialog
            dlgProgress.show();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread t = new Thread() {

                public void run() {

                    try {

                        // Create the ProcessRedemptionCheckoutMethods object
                        CustomerService customerService = new CustomerService();

                        // Get the returned data
                        customerSaveResponse = customerService.postRegisterCustomer(context, mobile,name,email,password);


                    } catch (Exception e) {
                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    // Post to the mUpdateCustomerRegistration runnable
                    mHandler.post(mUpdateCustomerRegistration);
                }
            };
            t.start();

        }

    }

    /**
     * Method to update the customer register response
     *
     */
    public void updateCustomerRegistration(){

        //Dismiss dialog
        dlgProgress.dismiss();

        //Get status
        String status=customerSaveResponse.getStatus();

        //Get errorcode
        String errorcode=customerSaveResponse.getErrorcode();

        if(status.equals("success")){

            //Log the error
            Log.d("customerRegistration", "Customer registration successfull");

            //show toast message
           // generalMethods.showToastMessage(context,"Customer registered successfully");

            showOtpValidatePage();

        }else{

            //Show toast based on error code
            generalMethods.showToastMessage(context,errorcode);

            //Log the error
            Log.d("error", errorcode);

        }
    }

    /**
     * Method to create customer object from customer save form
     * @return
     */
    public CustomerResource createCustomerObject(){

        CustomerResource customerResource = new CustomerResource();

        // set customer name
        customerResource.setFirstname(txtCusName.toString().trim());

        // set customer mobile
        customerResource.setMobile(mobileNo);

        // set customer password
        customerResource.setMobile(txtCusPassword.toString().trim());

        // set customer email
        customerResource.setMobile(txtCusEmail.toString().trim());

        return customerResource;

    }

    /**
     * function to validate the customer profile
     * This function is called when the "Register" button is clicked
     * on the Customer Profile screen
     *
     */
    public boolean validateCustomerProfile() {

        // Initially set the isValid flag to true
        boolean isValid = true;

        String mobileNo = txtCusMobileNo.getText().toString().trim();

        if(mobileNo.equals("")){

            txtCusMobileNo.setError(getString(R.string.err_customer_mobile));

            isValid=false;
        }

        String password = txtCusPassword.getText().toString().trim();

        if(password.equals("")){

            txtCusPassword.setError(getString(R.string.err_customer_password));

            isValid=false;
        }

        String name = txtCusName.getText().toString().trim();

        if(name.equals("")){

            txtCusName.setError(getString(R.string.err_customer_name));

            isValid=false;
        }

        String email = txtCusEmail.getText().toString().trim();

        if (email.equals("")){

            txtCusEmail.setError(getString(R.string.err_customer_email));

            isValid=false;
        }

        if ( !isValid) return false;

        return true;

    }

}
