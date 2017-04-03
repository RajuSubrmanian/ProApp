package com.inspirenetz.app.proapp.pay;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.adapter.RedemptionMerchantListSpinnerAdapter;
import com.inspirenetz.app.proapp.dictionary.RedemptionType;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.MembershipResource;
import com.inspirenetz.app.proapp.resources.PayMerchantBillsResponse;
import com.inspirenetz.app.proapp.resources.RedemptionPartnerResource;
import com.inspirenetz.app.proapp.resources.RedemptionPartnerResponse;
import com.inspirenetz.app.proapp.resources.UserInfoResource;
import com.inspirenetz.app.proapp.service.PayService;
import com.inspirenetz.app.proapp.service.RedemptionService;

import java.util.ArrayList;

/**
 * Created by raju on 2/24/17.
 */

public class PayMerchantBillsFragment extends Fragment{

    private View rootView;

    private LayoutInflater currInflater;

    private RelativeLayout layPayWithPointsContainer;

    private LinearLayout layPaymentStatusContainer;

    private Context context;

    private Dialog dlgProgress;

    private Spinner spnMerchantPartner;

    private Button btnPaySummaryContinue;

    //declare pay bill fields
    private EditText txtPayAmount;
    private EditText txtPayReference;
    private EditText txtPayPin;

    //keep merchant identifier
    private String merchantIdentifier = "";

    //keep redemption merchant name
    private String redemptionMerchantName = "";

    //keep loyalty id
    private String loyaltyId = "";

    private Button btnPayProceed;

    private RedemptionMerchantListSpinnerAdapter redemptionMerchantListSpinnerAdapter;

    private ArrayList<RedemptionPartnerResource> redemptionMerchantListResources = new ArrayList<RedemptionPartnerResource>();

    private RedemptionPartnerResponse redemptionPartnerResponse;

    private PayMerchantBillsResponse payMerchantBillsResponse;

    final Handler mHandler = new Handler();

    private GeneralMethods generalMethods = new GeneralMethods();

    //Runnable instance for mGetRedemptionPartnerList method
    final Runnable mGetRedemptionPartnerList = new Runnable() {

        @Override
        public void run() {

            updateRedemptionPartnerListResponse();
        }

    };

    //Runnable instance for mPostPayMerchantBill method
    final Runnable mPostPayMerchantBill = new Runnable() {

        @Override
        public void run() {

            updatePostPayMerchantBillResponse();
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
        rootView= inflater.inflate(R.layout.fragment_pay_merchant_bills, container, false);

        initialize();

        getRedemptionMerchantsList();

        //Get Arguments
        Bundle params=getArguments();

        if(params!=null){

            //setParams(params);
        }

        return rootView;
    }

    /**
     * Method to clear the redemption merchant list spinner
     * and add the default name to the spinner
     */
    public void clearPartnerMerchantList(){

        redemptionMerchantListResources.clear();

        //Add default entry
        RedemptionPartnerResource redemptionPartnerResource = new RedemptionPartnerResource();
        redemptionPartnerResource.setRemNo("0");
        redemptionPartnerResource.setRemName("Choose merchant partner");
        redemptionMerchantListResources.add(redemptionPartnerResource);

        redemptionMerchantListSpinnerAdapter.notifyDataSetChanged();
    }

    /**
     * Method to initialize ui components
     */
    public void initialize(){

        // Initialize pay bill relative layout
        layPayWithPointsContainer = (RelativeLayout)rootView.findViewById(R.id.layPayWithPointsContainer);

        // Initialize payment status linear layout
        layPaymentStatusContainer = (LinearLayout) rootView.findViewById(R.id.layPaymentStatusContainer);

        //initialize text fields of pay bill
        txtPayAmount = (EditText)rootView.findViewById(R.id.txtPayAmount);
        txtPayReference = (EditText)rootView.findViewById(R.id.txtPayReference);
        txtPayPin = (EditText)rootView.findViewById(R.id.txtPayPin);

        SessionManager sessionManager = new SessionManager(context);
        UserInfoResource userInfoResource = sessionManager.getUserInfo();
        loyaltyId = userInfoResource.getUsrLoginId();

        // Get redemption merchant spinner field
        spnMerchantPartner = (Spinner) rootView.findViewById(R.id.spnMerchantPartner);

        redemptionMerchantListSpinnerAdapter = new RedemptionMerchantListSpinnerAdapter(context, android.R.layout.simple_spinner_item, redemptionMerchantListResources);

        spnMerchantPartner.setAdapter(redemptionMerchantListSpinnerAdapter);

        spnMerchantPartner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                RedemptionPartnerResource redemptionPartnerResource = (RedemptionPartnerResource)parent.getItemAtPosition(position);

                merchantIdentifier = redemptionPartnerResource.getRemCode();

                redemptionMerchantName = redemptionPartnerResource.getRemName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnPayProceed = (Button)rootView.findViewById(R.id.btnPayProceed);

        btnPayProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postPayMerchantBill();
            }
        });

        // Click on sign in done button
        txtPayPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){

                    // call the method to pay the bill
                    postPayMerchantBill();
                }
                return false;
            }
        });

        btnPaySummaryContinue = (Button)rootView.findViewById(R.id.btnPaySummaryContinue);

        btnPaySummaryContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearPayForm();
            }
        });

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
        spnMerchantPartner.setSelection(0);

        // clear amount
        txtPayAmount.setText("");

        // clear reference
        txtPayReference.setText("");

        // clear otp
        txtPayPin.setText("");

    }

    /**
     * Method to get redemption merchant list
     * based on the merchant no
     */
    public void getRedemptionMerchantsList() {

        // get the Reference to the custom  progress dialog
        dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Merchants", "fetching partners...");

        // Show the dialog
        dlgProgress.show();

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread

        Thread thread = new Thread() {

            public void run() {

                try {

                    RedemptionService redemptionService = new RedemptionService();

                    redemptionPartnerResponse = redemptionService.getRedemptionPartnerList(context, "14");


                } catch (Exception e) {

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                }

                mHandler.post(mGetRedemptionPartnerList);
            }
        };
        thread.start();
    }

    /**
     * Method to get response and update
     * the redemption merchant list in the spinner
     */
    public void updateRedemptionPartnerListResponse() {

        //Dismiss dialog
        dlgProgress.dismiss();

        //Clear the resources
        clearPartnerMerchantList();

        // Get the status
        String status = redemptionPartnerResponse.getStatus();

        if (status.equals("success")) {

            ArrayList<RedemptionPartnerResource> redemptionPartnerResourceArrayList = (ArrayList<RedemptionPartnerResource>) redemptionPartnerResponse.getData();

            for(RedemptionPartnerResource redemptionPartnerResource : redemptionPartnerResourceArrayList){

                if(redemptionPartnerResource.getRemType().equals(String.valueOf(RedemptionType.REM_TYPE_UTILITY_MERCHANTS))){

                    redemptionMerchantListResources.add(redemptionPartnerResource);

                    redemptionMerchantListSpinnerAdapter.notifyDataSetChanged();
                }


            }

        } else if (status.equals("failed")) {

            String errorMessage = generalMethods.getTextualErrorString(context, redemptionPartnerResponse.getErrorcode());

            //Show toast based on error code
            generalMethods.showToastMessage(context,errorMessage);

        }
    }

    /**
     * Method to post pay bill details to server
     * based on the require details
     */
    public void postPayMerchantBill() {

        // get the Reference to the custom  progress dialog
        dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Pay bill", "process...");

        // Show the dialog
        dlgProgress.show();

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread

        Thread thread = new Thread() {

            public void run() {

                try {

                    String amount = txtPayAmount.getText().toString();
                    String ref = txtPayReference.getText().toString();
                    String pin = txtPayPin.getText().toString();

                    PayService payService = new PayService();

                    payMerchantBillsResponse = payService.postPayMerchantBill(context, "14", merchantIdentifier, ref ,amount, pin, "3","");


                } catch (Exception e) {

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                }

                mHandler.post(mPostPayMerchantBill);
            }
        };
        thread.start();
    }

    /**
     * Method to get response and update
     * the response of post pay bill
     */
    public void updatePostPayMerchantBillResponse() {

        //Dismiss dialog
        dlgProgress.dismiss();

        // Get the status
        String status = payMerchantBillsResponse.getStatus();

        if (status.equals("success")) {

           generalMethods.showToastMessage(context, "Payment success");

            populatePaymentStatus();

        } else if (status.equals("failed")) {

            String errorMessage = generalMethods.getTextualErrorString(context, payMerchantBillsResponse.getErrorcode());

            //Show toast based on error code
            generalMethods.showToastMessage(context,errorMessage);

        }
    }
    /**
     * Method to populate the payment details
     * after pay function
     * whether it sucess or failed
     */
    public void populatePaymentStatus(){

        // disable pay bill screen
        layPayWithPointsContainer.setVisibility(View.GONE);

        // enable payment status screen
        layPaymentStatusContainer.setVisibility(View.VISIBLE);

        // initialize ui components
        EditText txtTxnMerchantName = (EditText)rootView.findViewById(R.id.txtTxnMerchantName);
        EditText txtTxnCreditAcc = (EditText)rootView.findViewById(R.id.txtTxnCreditAcc);
        EditText txtTxnAmount = (EditText)rootView.findViewById(R.id.txtTxnAmount);
        EditText txtTxnReference = (EditText)rootView.findViewById(R.id.txtTxnReference);

        // set the values to the ui
        txtTxnMerchantName.setText(redemptionMerchantName);
        txtTxnCreditAcc.setText(redemptionMerchantName);
        txtTxnAmount.setText(txtPayAmount.getText().toString());
        txtTxnReference.setText(txtPayReference.getText().toString());

        btnPaySummaryContinue = (Button)rootView.findViewById(R.id.btnPaySummaryContinue);

        btnPaySummaryContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearPayForm();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }
}
