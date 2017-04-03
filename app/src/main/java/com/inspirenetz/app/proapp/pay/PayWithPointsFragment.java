package com.inspirenetz.app.proapp.pay;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sms.broadcast.SmsListener;
import com.android.sms.broadcast.SmsReceiver;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.inspirenetz.app.proapp.CustomerHomeActivity;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.adapter.MerchantsListSpinnerAdapter;
import com.inspirenetz.app.proapp.adapter.RedemptionMerchantListSpinnerAdapter;
import com.inspirenetz.app.proapp.adapter.RewardBalanceGalleryAdapter;
import com.inspirenetz.app.proapp.dictionary.OTPType;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.MembershipResource;
import com.inspirenetz.app.proapp.resources.MembershipResponse;
import com.inspirenetz.app.proapp.resources.OneTimePasswordResponse;
import com.inspirenetz.app.proapp.resources.PayWithPointsResource;
import com.inspirenetz.app.proapp.resources.PayWithPointsResponse;
import com.inspirenetz.app.proapp.resources.RewardBalanceResource;
import com.inspirenetz.app.proapp.resources.RewardBalanceResponse;
import com.inspirenetz.app.proapp.resources.UserInfoResource;
import com.inspirenetz.app.proapp.service.CustomerRewardBalanceService;
import com.inspirenetz.app.proapp.service.MembershipService;
import com.inspirenetz.app.proapp.service.OTPService;
import com.inspirenetz.app.proapp.service.PayService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raju on 27-Aug-16.
 */
public class PayWithPointsFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    private RelativeLayout layDebitAccount;

    private RelativeLayout layPayWithPointsContainer;

    private LinearLayout layPaymentStatusContainer;

    private TextInputLayout layCreditAccContainer;
    private TextInputLayout layAmountContainer;
    private TextInputLayout layReferenceContainer;
    private TextInputLayout layOtpContainer;

    private EditText txtCreditAcc;

    private EditText txtPayAmount;

    private EditText txtPayReference;

    private EditText txtPayOTP;

    private ImageView imgPayQRCode;

    private Spinner spnRedMerName;

    private Spinner spnPayMerchant;

    private Spinner spnPayRewardPoints;

    private TextView lblRedMerName;

    private TextView txtDebitAccountAvlBalance;

    private TextView txtDebitAccountMerchant;

    private TextView txtDebitAccountRewardName;

    private TextView txtDebitAccountPointDebit;

    private Button btnPayProceed;

    private Button btnPaySummaryContinue;

    String amount = "";

    String redemptionMerchantName = "";

    String redMerchantCode = "";

    // keep merchant no
    String merchantNo = "";

    String cusLoyaltyId = "";

    String rewardCurrencyName = "";

    double rewardBalance;

    double cashbackValue;

    double payAmount;

    double pointDebit;

    private Dialog dlgProgress;

    private OnFragmentInteractionListener mListener;

    private MembershipResponse membershipResponse;

    private GeneralMethods generalMethods = new GeneralMethods();

    final Handler mHandler = new Handler();

    private MerchantsListSpinnerAdapter merchantsListSpinnerAdapter;

    private RedemptionMerchantListSpinnerAdapter redemptionMerchantListSpinnerAdapter;

    private RewardBalanceGalleryAdapter rewardBalanceGalleryAdapter;

    private ArrayList<MembershipResource> merchantListResources = new ArrayList<MembershipResource>();

    private ArrayList<MembershipResource> redemptionMerchantListResources = new ArrayList<MembershipResource>();

    private ArrayList<RewardBalanceResource> rewardBalanceResources = new ArrayList<RewardBalanceResource>();

    private List<MembershipResource> membershipResources;

    private PayWithPointsResponse payWithPointsResponse;

    private RewardBalanceResponse rewardBalanceResponse;

    private OneTimePasswordResponse oneTimePasswordResponse;

    private SessionManager sessionManager;

    public PayWithPointsFragment(){

        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get context
        context = getActivity();

        // Get Inflator
        currInflater = inflater;

        // Create the Session Manager object
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        // Check the login
        sessionManager.checkLogin();

        // Create the GeneralMethod object
        GeneralMethods genMethod = new GeneralMethods();

        // Check if the network connection is available
        boolean isConnected = genMethod.isOnline(context);

        // If the connection not available
        // then show the toast message
        if(!isConnected){

            genMethod.showToastMessage(context,"Network Unavailable");

        }

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_customer_pay_with_points, container, false);

        initialize();

        // Get Arguments
        Bundle params = getArguments();

        if(params!=null){

            setParams(params);

        }

        // Return root view
        return rootView;

    }

    /**
     * Method to set Params
     * @param params
     */
    public void setParams(Bundle params){


    }

    /**
     * Method to clear the merchant list spinner
     * and add the default name to the spinner
     */
    public void clearPayMerchants(){

        merchantListResources.clear();

        //Add default entry
        MembershipResource membershipResource = new MembershipResource();
        membershipResource.setMerMerchantNo("0");
        membershipResource.setMerMerchantName("Choose Merchant");
        merchantListResources.add(membershipResource);

        merchantsListSpinnerAdapter.notifyDataSetChanged();
    }

    /**
     * Method to clear the reward name in the spinner
     * and add the default name to the spinner
     */
    public void clearRewardBalance(){

        rewardBalanceResources.clear();

        //Add default entry
        RewardBalanceResource rewardBalanceResource=new RewardBalanceResource();
        rewardBalanceResource.setRwd_balance(0.0);
        rewardBalanceResource.setCashback_value("0.0");
        rewardBalanceResource.setRwd_name("Choose Reward");
        rewardBalanceResources.add(rewardBalanceResource);

        rewardBalanceGalleryAdapter.notifyDataSetChanged();
    }

    /**
     * Method to initialize the ui components
     */
    public void initialize(){

        // Initialize pay with points relative layout
        layPayWithPointsContainer = (RelativeLayout)rootView.findViewById(R.id.layPayWithPointsContainer);

        // Initialize payment status relative layout
        layPaymentStatusContainer = (LinearLayout) rootView.findViewById(R.id.layPaymentStatusContainer);

        // Get debit account relative layout
        layDebitAccount = (RelativeLayout)rootView.findViewById(R.id.layDebitAccount);

        // Get credit account
        txtCreditAcc = (EditText)rootView.findViewById(R.id.txtCreditAcc);

        // Get amount filed
        txtPayAmount = (EditText)rootView.findViewById(R.id.txtPayAmount);

        // Get pay reference field
        txtPayReference = (EditText)rootView.findViewById(R.id.txtPayReference);

        // Get redemption merchant name
        lblRedMerName = (TextView)rootView.findViewById(R.id.lblRedMerName);

        // Get debit reward balance field
        txtDebitAccountAvlBalance = (TextView)rootView.findViewById(R.id.txtDebitAccountAvlBalance);

        // Get debit merchant name
        txtDebitAccountMerchant = (TextView)rootView.findViewById(R.id.txtDebitAccountMerchant);

        // Get debit reward name
        txtDebitAccountRewardName = (TextView)rootView.findViewById(R.id.txtDebitAccountRewardName);

        // Get point debit
        txtDebitAccountPointDebit = (TextView)rootView.findViewById(R.id.txtDebitAccountPointDebit);

        // Get pay otp filed
        txtPayOTP = (EditText)rootView.findViewById(R.id.txtPayOTP);

        // Get pay qr code image
        imgPayQRCode = (ImageView)rootView.findViewById(R.id.imgPayQRCode);

        // set on click listener for qr code scanner
        imgPayQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //enable camera permission
                enableCameraPermission();

                //startScan(rootView);
            }
        });

        // initialize proceed button
        btnPayProceed = (Button)rootView.findViewById(R.id.btnPayProceed);

        // set on click listener for proceed button
        btnPayProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postPayDetails();
            }
        });

        // clear pay function form when this page loaded
        clearPayForm();
    }

    /**
     * Method to enable the camera permission in mobile
     * for version of 6.0 M(Marshmallow)
     */
    public void enableCameraPermission(){

        //check permission status from shared preferences
        permissionStatus = getActivity().getSharedPreferences("permissionStatus",getActivity().MODE_PRIVATE);

        //check if camera permission is granted or not if granted go to else part
        //if not granted prompt the dialog message to the user to enable the camera permission
        if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CAMERA)){

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Need Permission");
                builder.setMessage("This app needs camera permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        requestPermissions(new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            } else if (permissionStatus.getBoolean(Manifest.permission.CAMERA, false)) {

                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Need Permission");
                builder.setMessage("This app needs camera permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getActivity(), "Go to Permissions to Grant Camera", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }  else {

                //just request the permission
                requestPermissions(new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
            }


            //store the camera permission status in shared preferences
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.CAMERA,true);
            editor.commit();

            //sessionManager.storeCameraPermissionStatus(Manifest.permission.CAMERA, true);

        } else {

            //You already have the permission, just go ahead.
            startScan(rootView);
        }

    }

    //Handle the permissions request response
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //The Camera Permission is granted to you... Continue your left job...
                startScan(rootView);

            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Need Camera Permission");
                    builder.setMessage("This app needs camera permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                } else {

                    Toast.makeText(context,"Unable to get Permission",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Function to start the scan
     * @param view - The view object calling the function
     */
    public void startScan(View view) {

        //initially clear the pay form
        clearPayForm();

        //initiate the scan
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //check camera permission status result
        if (requestCode == REQUEST_PERMISSION_SETTING) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                startScan(rootView);
            }

        }else {

            // Store the scanning result
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

            // Check if the result is null,
            if (scanningResult != null) {

                // get the scan content
                String scanContent = scanningResult.getContents();

                if(scanContent == null){

                   clearPayForm();

                }else {

                    String[] scanContentArray = scanContent.split("#");

                    if ((!scanContent.contains("#")) || (scanContentArray.length > 3) || (scanContentArray.length != 3)) {

                        generalMethods.showErrorPopupMessage(context);

                    }else {

                        // populate scanned data into ui
                        populateScanData(scanContent);
                    }
                }

            } else {

                generalMethods.showToastMessage(context, "No scan data received!");

            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sentToSettings) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                //Got Permission
                startScan(rootView);
            }
        }
    }

    /**
     * Method to populate the qr code scanned data to ui
     * @param scanData after scan qr code data
     */
    public void populateScanData(String scanData){

        /*StringTokenizer strTkn = new StringTokenizer(subject, delimiters);
        ArrayList<String> arrLis = new ArrayList<String>(subject.length());

        while(strTkn.hasMoreTokens())
            arrLis.add(strTkn.nextToken());

        return arrLis.toArray(new String[0]);*/

        // split the qr code data in string array using #
        String[] scanDataArray = scanData.split("#");

        String merchantCode = scanDataArray[0];
        redemptionMerchantName = scanDataArray[1];
        String mobileNo = scanDataArray[2];

        // Set to the credit acc field
        txtCreditAcc.setText(mobileNo);

        // Set to the redemption merchant name field
        lblRedMerName.setText(redemptionMerchantName);

        // set to string
        redMerchantCode = merchantCode;

        //getMerchantsList();

        sendPayOTP();

    }

    // Runnable instance for getMerchantsList method
    final Runnable mGetMerchantList = new Runnable() {
        @Override
        public void run() {
            updateMerchantList();
        }
    };

    /**
     * Method to get merchant list
     * based on the redemption merchant code
     */
    public void getMerchantsList() {

        // get the Reference to the custom  progress dialog
        dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Merchants", "fetching merchants...");

        // Show the dialog
        dlgProgress.show();

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread

        Thread thread = new Thread() {

            public void run() {

                try {

                    MembershipService membershipService = new MembershipService();

                    membershipResponse = membershipService.getMerchantList(context, redMerchantCode);


                } catch (Exception e) {

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                }

                mHandler.post(mGetMerchantList);
            }
        };
        thread.start();
    }

    /**
     * Method to get response and update
     * the merchant list in the spinner
     */
    public void updateMerchantList() {

        //Clear the resources
        clearPayMerchants();

        // Get the status
        String status = membershipResponse.getStatus();

        if (status.equals("success")) {

            ArrayList<MembershipResource> membershipResourceList = (ArrayList<MembershipResource>) membershipResponse.getData();

            merchantListResources.addAll(membershipResourceList);

            merchantsListSpinnerAdapter.notifyDataSetChanged();

            //Dismiss dialog
            dlgProgress.dismiss();

        } else if (status.equals("failed")) {

            //Show toast based on error code
            generalMethods.showToastMessage(context, membershipResponse.getErrorcode());

            //Dismiss dialog
            dlgProgress.dismiss();

        }
    }


    // Runnable instance for pay proceed
    final Runnable mPostPayDetails = new Runnable() {
        @Override
        public void run() {
            updatePayDetailsResponse();
        }
    };

    /**
     * Method to post pay details
     * which is available in the pay form ui
     */
    public void postPayDetails() {

        if(validatePayFormDetails()) {

            // get the Reference to the custom  progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Pay", "In progress...");

            // Show the dialog
            dlgProgress.show();

            final PayWithPointsResource payWithPointsResource = createPayWithPointsObject();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread

            Thread thread = new Thread() {

                public void run() {

                    try {

                        PayService payService = new PayService();

                        payWithPointsResponse = payService.postPayWithPoints(context, payWithPointsResource);


                    } catch (Exception e) {

                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    mHandler.post(mPostPayDetails);
                }
            };
            thread.start();

        }
    }

    /**
     * Method to get response and update
     * status in the ui
     */
    public void updatePayDetailsResponse() {

        //Dismiss dialog
        dlgProgress.dismiss();

        // Get the status
        String status = payWithPointsResponse.getStatus();

        if (status.equals("success")) {

            generalMethods.showToastMessage(context, "pay success");

            PayWithPointsResource payWithPointsResource = payWithPointsResponse.getData();

            // populate payment status
            populatePaymentStatus();

            // clear the pay form
            //clearPayForm();

        } else if (status.equals("failed")) {

            String errorCode = payWithPointsResponse.getErrorcode();

            if(errorCode.equals("ERR_OTP_VALIDATION_FAILED")){

                //Show toast based on error code
                generalMethods.showToastMessage(context, "Invalid OTP");

                return;

            }else {

                //Show toast based on error code
                generalMethods.showToastMessage(context, payWithPointsResponse.getErrorcode());

                // clear the pay form
                clearPayForm();
            }

        }
    }

    /**
     * Method to populate the payment details
     * after pay function
     * whether it sucess or failed
     */
    public void populatePaymentStatus(){

        // disable pay with points screen
        layPayWithPointsContainer.setVisibility(View.GONE);

        // enable payment status screen
        layPaymentStatusContainer.setVisibility(View.VISIBLE);

        // initialize ui components
        EditText txtTxnMerchantName = (EditText)rootView.findViewById(R.id.txtTxnMerchantName);
        EditText txtTxnCreditAcc = (EditText)rootView.findViewById(R.id.txtTxnCreditAcc);
        EditText txtTxnAmount = (EditText)rootView.findViewById(R.id.txtTxnAmount);
        EditText txtTxnReference = (EditText)rootView.findViewById(R.id.txtTxnReference);
        /*TextView txtTxnPaidWith = (TextView)rootView.findViewById(R.id.txtTxnPaidWith);
        TextView txtTxnPointsDebit = (TextView)rootView.findViewById(R.id.txtTxnPointsDebit);*/

        // set the values to the ui
        txtTxnMerchantName.setText(redemptionMerchantName);
        txtTxnCreditAcc.setText(txtCreditAcc.getText().toString());
        txtTxnAmount.setText(txtPayAmount.getText().toString());
        txtTxnReference.setText(txtPayReference.getText().toString());
        /*txtTxnPaidWith.setText(rewardCurrencyName);
        txtTxnPointsDebit.setText(String.valueOf(pointDebit));*/

        btnPaySummaryContinue = (Button)rootView.findViewById(R.id.btnPaySummaryContinue);

        btnPaySummaryContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearPayForm();
            }
        });

        /*// initialize view my transaction button
        Button btnViewMyTransactions = (Button)rootView.findViewById(R.id.btnViewMyTransactions);

        // set Onclick listener for the view my transaction button
        btnViewMyTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putSerializable("myTransaction",merchantNo);

                mListener.navigateToFragment(bundle, ApplicationWindows.MY_TRANSACTION_SCREEN);
            }
        });*/

    }

    /**
     * method to create object for PayWithPointsResource
     * to proceed the pay function
     * @return PayWithPointsResource object
     */
    public PayWithPointsResource createPayWithPointsObject(){

        PayWithPointsResource payWithPointsResource = new PayWithPointsResource();

        // set merchant no
        payWithPointsResource.setMerchantNo("14");

        // set merchant identifier
        payWithPointsResource.setMerchantIdentifier(redMerchantCode);

        // set amount
        payWithPointsResource.setAmount(txtPayAmount.getText().toString().trim());

        // set reference
        payWithPointsResource.setRef(txtPayReference.getText().toString().trim());

        // set pay otp
        payWithPointsResource.setOtpCode(txtPayOTP.getText().toString().trim());

        // set channel
        payWithPointsResource.setChannel("3");

        return payWithPointsResource;
    }

    /**
     * Method to validate the pay form details
     * when click the proceed button
     * @return
     */
    public boolean validatePayFormDetails(){

        boolean isValid = true;

        String amount = txtPayAmount.getText().toString();

        if(amount.equals("")){

            txtPayAmount.setError(getString(R.string.err_pay_amount));

            isValid = false;
        }

        String reference = txtPayReference.getText().toString();

        if(reference.equals("")){

            txtPayReference.setError(getString(R.string.err_pay_reference));

            isValid = false;
        }

        String otp = txtPayOTP.getText().toString();

        if(otp.equals("")){

            txtPayOTP.setError(getString(R.string.err_pay_otp));

            isValid = false;
        }

        if(!isValid) return false;

        return isValid;
    }

    /**
     * Method to clear the pay form after proceed the pay function
     * for next pay function
     */
    public void clearPayForm(){

        // disable pay with points screen
        layPayWithPointsContainer.setVisibility(View.VISIBLE);

        // enable payment status screen
        layPaymentStatusContainer.setVisibility(View.GONE);

        // clear redemption merchant name
        lblRedMerName.setText("Redemption Merchant Name");

        // clear cashier mobile no
        txtCreditAcc.setText("");

        // clear amount
        txtPayAmount.setText("");

        // clear reference
        txtPayReference.setText("");

        // clear otp
        txtPayOTP.setText("");

        //clearPayMerchants();

        //clear merchant spinner
        //spnPayMerchant.setSelection(0);

        //clearRewardBalance();

        // clear reward name spinner
        //spnPayRewardPoints.setSelection(0);

        // clear available balance text view
        txtDebitAccountAvlBalance.setText("0.0");

        pointDebit = 0.0;

        // clear point debit
        txtDebitAccountPointDebit.setText("0.0");

        return;

    }


    // Runnable instance for get customer reward balance
    final Runnable mGetCustomerRewardBalance = new Runnable() {
        @Override
        public void run() {
            updateGetRewardBalanceResponse();
        }
    };

    /**
     * Method to get the customer reward balance
     * based on the customer loyalty id
     * when the merchant selected in the spinner
     */
    public void getCustomerRewardBalanceDetails() {

            // get the Reference to the custom  progress dialog
            dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Reward", "fetching details...");

            // Show the dialog
            dlgProgress.show();

            // Fire off a thread to do some work that we shouldn't do directly in the UI thread
            Thread thread = new Thread() {

                public void run() {

                    try {

                        // Create the CustomerRewardBalanceService object
                        CustomerRewardBalanceService customerRewardBalanceService = new CustomerRewardBalanceService();

                        /// Hold the response
                        rewardBalanceResponse = customerRewardBalanceService.getRewardBalance(context, merchantNo);


                    } catch (Exception e) {

                        Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                    }

                    mHandler.post(mGetCustomerRewardBalance);
                }
            };
            thread.start();
    }

    /**
     * Method to get response and update
     * status in the reward spinner
     */
    public void updateGetRewardBalanceResponse() {

        //Dismiss dialog
        dlgProgress.dismiss();

        //Clear the resources
        clearRewardBalance();

        // Get the status
        String status=rewardBalanceResponse.getStatus();

        if(status.equals("success")){

            //Get reward balance from response
            List<RewardBalanceResource> rewardBalanceResourceList = rewardBalanceResponse.getData();

            //Check whether reward balance list is null or size zero or not
            if(rewardBalanceResourceList!=null && rewardBalanceResourceList.size()>0){

                //add to reward balance list to reflect in spinner
                rewardBalanceResources.addAll(rewardBalanceResourceList);
            }

            rewardBalanceGalleryAdapter.notifyDataSetChanged();

        }else if ( status.equals("failed")) {

            // Log error
            Log.d("updateRewardBalance", "Reward Balance list fetching failed");

            //Show toast based on error code
            generalMethods.showToastMessage(context,rewardBalanceResponse.getErrorcode());

        }
    }

    // Runnable instance for send the pay with points OTP method
    final Runnable mSendPayOTP = new Runnable() {
        @Override
        public void run() {

            updatePayOTPStatus();
        }
    };

    /**
     * Method to send pay otp
     * and send to customer
     */
    public void sendPayOTP(){

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread thread = new Thread(){

            public void run(){

                try{

                    SessionManager sessionManager = new SessionManager(context);
                    UserInfoResource userInfoResource = sessionManager.getUserInfo();
                    String mobileNo = userInfoResource.getUsrLoginId();

                    String otpType = String.valueOf(OTPType.PAY_WITH_POINTS);

                    // Create service object for OTPService class
                    OTPService otpService = new OTPService();

                    // Hold the response
                    oneTimePasswordResponse = otpService.generatePayOTP(context, mobileNo, "14", otpType);


                }catch (Exception e){

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                }

                // Post to the mSendPayOTP runnable
                mHandler.post(mSendPayOTP);

            }
        };
        thread.start();
    }

    /**
     * Method to display OTP message
     */
    public void updatePayOTPStatus(){

        if(oneTimePasswordResponse.getStatus().equals("success")){

            //Show toast Message
            generalMethods.showToastMessage(context,"OTP have been sent");

            SmsReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {

                    txtPayOTP.setText(messageText);
                }
            });

        }else{

            Log.d("pay otp response", "sending failed");

            //show error in toast
            generalMethods.showToastMessage(context,oneTimePasswordResponse.getErrorcode());
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        //redirect to  fragment with uri
        public void navigateToFragment(Bundle bundle, Integer uri);
    }

}
