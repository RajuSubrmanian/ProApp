package com.inspirenetz.app.proapp.customer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.common.util.concurrent.Runnables;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.promotions.PromotionListFragment;
import com.inspirenetz.app.proapp.resources.CustomerProfileInfoResource;
import com.inspirenetz.app.proapp.resources.CustomerProfileInfoResponse;
import com.inspirenetz.app.proapp.resources.CustomerProfileInfoUpdateResponse;
import com.inspirenetz.app.proapp.resources.CustomerResource;
import com.inspirenetz.app.proapp.resources.UserInfoResource;
import com.inspirenetz.app.proapp.service.CustomerService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by raju on 2/5/17.
 */

public class CustomerProfileDetailsFragment extends Fragment {

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    //Declare mandatory fields
    private EditText txtCusFirstName;
    private EditText txtCusLastName;
    private EditText txtCusEmail;

    //Declare personal information
    private RadioGroup radioGroupGender;
    private EditText txtCusBirthday;
    private RadioGroup radioGroupMaritalStatus;
    private EditText txtCusAnniversary;

    private RadioButton radioButtonGender;
    private RadioButton radioButtonMaritalStatus;

    private RadioButton radio_male;
    private RadioButton radio_female;
    private RadioButton radio_single;
    private RadioButton radio_married;

    private TextView lblCusAnniversary;

    private TextInputLayout layCusAnniversaryContainer;

    //Declare update profile button
    private Button btnUpdateProfile;

    //Declare DatePickerDialog for from date
    private DatePickerDialog dpdCusBirthDay;

    //Declare DatePickerDialog for to date
    private DatePickerDialog dpdCusAnniversary;

    //Date format for app
    private SimpleDateFormat dateFormatter;

    //Date format for api
    private SimpleDateFormat apiDateFormatter;

    //keep mobile no
    private String mobileNO = "";

    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    GeneralMethods generalMethods=new GeneralMethods();

    private PromotionListFragment.OnFragmentInteractionListener mListener;

    private CustomerProfileInfoResponse customerProfileInfoResponse;

    private CustomerProfileInfoUpdateResponse customerProfileInfoUpdateResponse;

    //Runnable instance for get customer profile details
    final Runnable mGetCustomerProfileDetails = new Runnable() {
        @Override
        public void run() {

            updateGetCustomerProfileInfo();
        }
    };

    //Runnable instance for post customer profile details
    final Runnable mPostCustomerProfileDetails = new Runnable() {
        @Override
        public void run() {

            updateCustomerProfileInfoResponse();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Save the context
        this.context = getActivity();

        // Store the inflater reference
        currInflater = inflater;

        // Create the session maanger object
        SessionManager session = new SessionManager(getActivity().getApplicationContext());

        // Check the login
        session.checkLogin();


        // Create the GeneralMethods object
        GeneralMethods genMethods = new GeneralMethods();


        // Check if the network connection is available
        boolean isConnected = genMethods.isOnline(context);

        // If the connection is not available
        // then show the toast message
        if ( !isConnected ) {

            genMethods.showToastMessage(context, "Network Unavailable");

        }

        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_customer_profile_details, container, false);

        UserInfoResource userInfoResource = session.getUserInfo();

        mobileNO = userInfoResource.getUsrLoginId();

        initialize();

        getCustomerProfileDetails();

        //Get Arguments
        Bundle params=getArguments();

        if(params!=null){

            //setParams(params);
        }

        return rootView;
    }

    /**
     * Method to initialize ui components
     */
    public void initialize(){

        //initializer mandatory fields
        txtCusFirstName = (EditText)rootView.findViewById(R.id.txtCusFirstName);
        txtCusLastName = (EditText)rootView.findViewById(R.id.txtCusLastName);
        txtCusEmail = (EditText)rootView.findViewById(R.id.txtCusEmail);

        //initialize personal information
        radioGroupGender = (RadioGroup)rootView.findViewById(R.id.radioGroup);
        radioGroupMaritalStatus = (RadioGroup)rootView.findViewById(R.id.radioGroupMaritalStatus);
        txtCusBirthday = (EditText)rootView.findViewById(R.id.txtCusBirthday);
        txtCusAnniversary = (EditText)rootView.findViewById(R.id.txtCusAnniversary);

        layCusAnniversaryContainer = (TextInputLayout)rootView.findViewById(R.id.layCusAnniversaryContainer);

        //initialize radio button fields
        radio_male  = (RadioButton) rootView.findViewById(R.id.radio_male);
        radio_female  = (RadioButton) rootView.findViewById(R.id.radio_female);
        radio_single  = (RadioButton) rootView.findViewById(R.id.radio_single);
        radio_married  = (RadioButton) rootView.findViewById(R.id.radio_married);

        lblCusAnniversary = (TextView)rootView.findViewById(R.id.lblCusAnniversary);

        txtCusBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dpdCusBirthDay.show();
            }
        });

        txtCusAnniversary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dpdCusAnniversary.show();
            }
        });

        //initialize button
        btnUpdateProfile = (Button)rootView.findViewById(R.id.btnUpdateProfile);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postCustomerProfileInfo();
            }
        });

        txtCusBirthday.setInputType(InputType.TYPE_NULL);

        txtCusAnniversary.setInputType(InputType.TYPE_NULL);

        //instantiate date formatter for api
        apiDateFormatter=generalMethods.getServerDateFormat();

        //instantiate app date formatter
        dateFormatter = generalMethods.getAppDateFormat();

        // get calender
        Calendar newCalendar = Calendar.getInstance();

        // set date picker for customer birthday date edit text field
        dpdCusBirthDay = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtCusBirthday.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        // set date picker for customer anniversary edit text field
        dpdCusAnniversary = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtCusAnniversary.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    /**
     * Method to fetch the customer profile details
     */
    public void getCustomerProfileDetails(){

            //Create a custom progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Customer","Fetching Profile...");

            // Show dialog
            dlgProgress.show();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread thread = new Thread(){

                public void run(){

                    try{

                        // Create service object for CustomerService class
                        CustomerService customerService = new CustomerService();

                        // Hold the response
                        customerProfileInfoResponse = customerService.getCustomerInfoWithMerchantNo(context, "14");


                    }catch (Exception e){

                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    // Post to the mUpdateCustomerSearch runnable
                    mHandler.post(mGetCustomerProfileDetails);

                }
            };
            thread.start();
    }

    /**
     * Method to update the customer profile details
     * after call the api
     * get the response from the server
     */
    public void updateGetCustomerProfileInfo(){

        // Dismiss dialog
        dlgProgress.dismiss();

        // Get status
        String status = customerProfileInfoResponse.getStatus();


        if(status.equals("success")){

            CustomerProfileInfoResource customerProfileInfoResource = customerProfileInfoResponse.getData();

            populateCustomerProfileDetails(customerProfileInfoResource);

        }else{

            String errorMessage = generalMethods.getTextualErrorString(context, customerProfileInfoResponse.getErrorcode());

            // Show error message
            generalMethods.showToastMessage(context, errorMessage);

        }

    }

    /**
     * Method to populate the customer profile details
     * after getting the response from the api
     * @param customerProfileInfoResource pass resource object
     */
    public void populateCustomerProfileDetails(CustomerProfileInfoResource customerProfileInfoResource){

        //set first name
        txtCusFirstName.setText(customerProfileInfoResource.getCusFName());

        //set last name
        txtCusLastName.setText(customerProfileInfoResource.getCusLName());

        //set email
        txtCusEmail.setText(customerProfileInfoResource.getCusEmail());

        String cusBirthday = customerProfileInfoResource.getCspCustomerBirthday();

        try {

            if(!cusBirthday.equals("")){

                cusBirthday = dateFormatter.format(apiDateFormatter.parse(cusBirthday.trim()));

                //set birthday
                txtCusBirthday.setText(cusBirthday);
            }

        }catch (Exception e){

            txtCusBirthday.setText("");
        }

        String cusAnniversary = customerProfileInfoResource.getCspCustomerAnniversary();

        try {

            if(!cusAnniversary.equals("")){

                cusAnniversary = dateFormatter.format(apiDateFormatter.parse(cusAnniversary.trim()));

                //set anniversary
                txtCusAnniversary.setText(cusAnniversary);
            }

        }catch (Exception e){

            txtCusAnniversary.setText("");
        }

        //set customer gender
        String cusGender = customerProfileInfoResource.getCspGender();

        if(cusGender.equals("F")){

            radio_female.setChecked(true);
        }

        //set customer family status
        String cusMaritalStatus = customerProfileInfoResource.getCspFamilyStatus();

        if(cusMaritalStatus.equals("2")){

            layCusAnniversaryContainer.setVisibility(View.VISIBLE);

            radio_married.setChecked(true);

        }else {

            layCusAnniversaryContainer.setVisibility(View.GONE);
        }

    }

    /**
     * Method to post the customer profile details
     */
    public void postCustomerProfileInfo(){

        if(validateCustomerProfileDetails()) {

            //Create a custom progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Update", "In Progress...");

            // Show dialog
            dlgProgress.show();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread thread = new Thread() {

                public void run() {

                    try {

                        CustomerProfileInfoResource customerProfileInfoResource = createCustomerResourceObject();

                        // Create service object for CustomerService class
                        CustomerService customerService = new CustomerService();

                        // Hold the response
                        customerProfileInfoUpdateResponse = customerService.postCustomerProfileInfoUpdate(context, customerProfileInfoResource);


                    } catch (Exception e) {

                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    // Post to the mUpdateCustomerSearch runnable
                    mHandler.post(mPostCustomerProfileDetails);

                }
            };
            thread.start();
        }
    }

    /**
     * Method to get the response of update customer profile
     * after call the api
     * get the response from the server
     */
    public void updateCustomerProfileInfoResponse(){

        // Dismiss dialog
        dlgProgress.dismiss();

        // Get status
        String status = customerProfileInfoUpdateResponse.getStatus();


        if(status.equals("success")){

            generalMethods.showToastMessage(context, "Updated Successfully...");

            getCustomerProfileDetails();

        }else{

            String errorMessage = generalMethods.getTextualErrorString(context, customerProfileInfoUpdateResponse.getErrorcode());

            // Show error message
            generalMethods.showToastMessage(context, errorMessage);

        }

    }

    /**
     * Method to create object for CustomerProfileInfoResource
     * to post the customer profile details
     * @return resource object
     */
    public CustomerProfileInfoResource createCustomerResourceObject(){

        CustomerProfileInfoResource customerProfileInfoResource = new CustomerProfileInfoResource();

        //get all fields
        String firstName = txtCusFirstName.getText().toString().trim();
        String lastName = txtCusLastName.getText().toString().trim();
        String email = txtCusEmail.getText().toString().trim();
        String birthday = txtCusBirthday.getText().toString().trim();
        String anniversary = txtCusAnniversary.getText().toString().trim();

        //change birth date format
        try {

            if(!birthday.equals("")){

                birthday = apiDateFormatter.format(dateFormatter.parse(birthday.toString().trim()));

                customerProfileInfoResource.setCspCustomerBirthday(birthday);

            }

        }catch (Exception e){

            customerProfileInfoResource.setCspCustomerBirthday("");
        }

        //change anniversary date format
        try {

            if(!anniversary.equals("")){

                anniversary = apiDateFormatter.format(dateFormatter.parse(anniversary.toString().trim()));

                customerProfileInfoResource.setCspCustomerAnniversary(anniversary);
            }

        }catch (Exception e){

            txtCusAnniversary.setText("");
        }

        // get selected radio button from radioGroup
        int genderValue = radioGroupGender.getCheckedRadioButtonId();

        // find the radio button by returned id
        radioButtonGender = (RadioButton)rootView.findViewById(genderValue);
        String cusGender = radioButtonGender.getText().toString();
        if(cusGender.equals("Male")){

            customerProfileInfoResource.setCspGender("M");

        }else {

            customerProfileInfoResource.setCspGender("F");
        }

        // get selected radio button from radioGroup
        int statusValue = radioGroupMaritalStatus.getCheckedRadioButtonId();

        // find the radio button by returned id
        radioButtonMaritalStatus = (RadioButton)rootView.findViewById(statusValue);
        String cusMaritalStatus = radioButtonMaritalStatus.getText().toString();
        if(cusMaritalStatus.equals("Single")){

            customerProfileInfoResource.setCspFamilyStatus("1");

        }else {

            customerProfileInfoResource.setCspFamilyStatus("2");
        }

        //set all fields
        customerProfileInfoResource.setCusFName(firstName);
        customerProfileInfoResource.setCusLName(lastName);
        customerProfileInfoResource.setCusEmail(email);
        customerProfileInfoResource.setCusMerchantNo("14");
        customerProfileInfoResource.setCusMobile(mobileNO);

        return customerProfileInfoResource;
    }

    /**
     * Method to validate the customer profile details
     * @return true or false
     */
    public boolean validateCustomerProfileDetails(){

        boolean isValid = true;

        //get all fields
        String firstName = txtCusFirstName.getText().toString().trim();
        String lastName = txtCusLastName.getText().toString().trim();
        String email = txtCusEmail.getText().toString().trim();
        String birthday = txtCusBirthday.getText().toString().trim();
        String anniversary = txtCusAnniversary.getText().toString().trim();

        if(firstName.equals("")){

            txtCusFirstName.setError(getString(R.string.err_cus_first_name));

            isValid = false;

        }

        if(lastName.equals("")){

            txtCusLastName.setError(getString(R.string.err_cus_last_name));

            isValid = false;

        }

        if(email.equals("")){

            txtCusEmail.setError(getString(R.string.err_cus_email));

            isValid = false;

        }

       /* if(birthday.equals("")){

            txtCusBirthday.setError(getString(R.string.err_cus_birth_date));

            isValid = false;

        }

        if(anniversary.equals("")){

            txtCusAnniversary.setError(getString(R.string.err_cus_anniversary_date));

            isValid = false;

        }*/

        if(!isValid) return false;

        return  isValid;
    }
}
