package com.inspirenetz.app.proapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.inspirenetz.app.proapp.resources.CustomerResource;
import com.inspirenetz.app.proapp.resources.CustomerSearchResponse;
import com.inspirenetz.app.proapp.service.CustomerService;

public class MainActivity extends Activity {

    // Declare context
    private Context context;

    // Declare mobile number;
    private EditText txtCusMobile;

    // Declare start button
    private Button btnStart;

    // Keep mobile no in string
    private String mobileNo = "";

    // Declare dialog
    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    //Create an instance of general method
    final GeneralMethods generalMethods = new GeneralMethods();

    private CustomerSearchResponse customerSearchResponse;


    public MainActivity() {

        // Empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Set the orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Make full screen app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create session manager object
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_main);

        initialize();
    }

    // Runnable instance for cash back transact method
    final Runnable rCheckCustomerMobileSearch = new Runnable() {
        @Override
        public void run() {

            updateCustomerMobileSearch();
        }
    };

    /**
     * Method to initialize the fields
     */
    protected void initialize(){

        // Get context
        context = MainActivity.this;

        // Get mobile number
        txtCusMobile = (EditText)findViewById(R.id.txtCusMobile);

        // Get start button
        btnStart = (Button)findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCustomerMobile();
            }
        });

        // Start click done button
        txtCusMobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // call the method to check customer
                    checkCustomerMobile();

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

        intent.putExtra("mobileNO", mobileNo);

        startActivity(intent);

        finish();
    }


    /**
     * Method to redirect to register activity
     */
    protected void showRegisterPage(){

        Intent intent=new Intent(this,RegisterActivity.class);

        intent.putExtra("mobileNO", mobileNo);

        startActivity(intent);

        finish();
    }

    /**
     * Method to authenticate the customer mobile number
     */
    public void checkCustomerMobile(){

        mobileNo = txtCusMobile.getText().toString();

        if(mobileNo.equals("")){

            txtCusMobile.setError(getString(R.string.err_customer_mobile));

        }else{

            //Create a custom progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Customer","Validating customer..");

            // Show dialog
            dlgProgress.show();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread thread = new Thread(){

                public void run(){

                    try{

                        // Create service object for CustomerService class
                        CustomerService customerService = new CustomerService();

                        // Hold the response
                        customerSearchResponse = customerService.getCustomerInfo(context, mobileNo);


                    }catch (Exception e){

                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    // Post to the mUpdateCustomerSearch runnable
                    mHandler.post(rCheckCustomerMobileSearch);

                }
            };
            thread.start();
        }
    }

    /**
     * Method to update the customer mobile seach
     * after call the api
     * get the response from the server
     */
    public void updateCustomerMobileSearch(){

        // Dismiss dialog
        dlgProgress.dismiss();

        // Get status
        String status = customerSearchResponse.getStatus();

        // Get error code
        String errorCode = customerSearchResponse.getErrorcode();

        if(status.equals("success")){

            CustomerResource customerResource = customerSearchResponse.getData();

            final String inputMobileNo = txtCusMobile.getText().toString();

            final  String responseMobileNo = customerResource.getMobile();

            if(inputMobileNo.equals(responseMobileNo)){

                //generalMethods.showToastMessage(context, "Valid Customer");

                showLoginPage();

            }else{

                generalMethods.showToastMessage(context, "Invalid Customer");
            }

        }else if(errorCode.equals("ERR_NO_INFORMATION")){

            showRegisterPage();

        }else{

            // Show error message
            generalMethods.showToastMessage(context, errorCode);

        }

    }
}
