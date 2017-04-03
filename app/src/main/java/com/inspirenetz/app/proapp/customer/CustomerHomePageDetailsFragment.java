package com.inspirenetz.app.proapp.customer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.adapter.CustomerActivityListViewAdapter;
import com.inspirenetz.app.proapp.adapter.RewardBalanceGalleryAdapter;
import com.inspirenetz.app.proapp.dictionary.ApplicationWindows;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.promotions.PromotionListFragment;
import com.inspirenetz.app.proapp.resources.CustomerActivityListResource;
import com.inspirenetz.app.proapp.resources.CustomerActivityListResponse;
import com.inspirenetz.app.proapp.resources.CustomerRewardBalanceResponse;
import com.inspirenetz.app.proapp.resources.RewardBalanceResource;
import com.inspirenetz.app.proapp.resources.RewardBalanceResponse;
import com.inspirenetz.app.proapp.resources.UserInfoResource;
import com.inspirenetz.app.proapp.service.CustomerRewardBalanceService;
import com.inspirenetz.app.proapp.service.CustomerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raju on 2/3/17.
 */

public class CustomerHomePageDetailsFragment extends Fragment {

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    //Declare text view's
    private TextView lblCusLoyaltyId;
    private TextView lblRewardPoints;
    private TextView lblRewardCurrency;

    //declare gallery view
    private Gallery rewardBalanceGallery;

    //Declare button
    private Button btnPayWithPoints;

    //Declare list view
    private ListView listView;

    //keep loyalty id
    String cusLoyaltyId = "";

    private Dialog dlgProgress;

    private OnFragmentInteractionListener mListener;

    private RewardBalanceResponse rewardBalanceResponse;

    private CustomerActivityListResponse customerActivityListResponse;

    private ArrayList<CustomerActivityListResource> customerActivityListResources = new ArrayList<CustomerActivityListResource>();

    private CustomerActivityListViewAdapter customerActivityListViewAdapter;

    private RewardBalanceGalleryAdapter rewardBalanceGalleryAdapter;

    private ArrayList<RewardBalanceResource> rewardBalanceResources = new ArrayList<RewardBalanceResource>();

    final Handler mHandler = new Handler();

    GeneralMethods generalMethods=new GeneralMethods();

    //Runnable instance for mUpdateCustomerRewardBalance method
    final Runnable mUpdateCustomerRewardBalance = new Runnable() {

        @Override
        public void run() {
            updateCustomerRewardBalanceResponse();
        }

    };

    //Runnable instance for mUpdateCustomerActivityList method
    final Runnable mUpdateCustomerActivityList = new Runnable() {

        @Override
        public void run() {

            updateCustomerActivityListResponse();
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
        rootView= inflater.inflate(R.layout.fragment_customer_home_page_details, container, false);

        initialize();

        //get customer reward balance details
        getCustomerRewardBalanceDetails();

        //get customer activities
        getCustomerActivityDetails();

        //Get Arguments
        Bundle params=getArguments();

        if(params!=null){

            //setParams(params);
        }

        return rootView;
    }

    /**
     * Method to initialize the ui components
     */
    public void initialize(){

        /*//Initialize text view components
        lblCusLoyaltyId = (TextView)rootView.findViewById(R.id.lblCusLoyaltyId);
        lblRewardPoints = (TextView)rootView.findViewById(R.id.lblRewardPoints);
        lblRewardCurrency = (TextView)rootView.findViewById(R.id.lblRewardCurrency);*/

        rewardBalanceGallery = (Gallery)rootView.findViewById(R.id.rewardBalanceGallery);

        rewardBalanceGalleryAdapter = new RewardBalanceGalleryAdapter(context, R.layout.content_of_gallery_adapter, rewardBalanceResources);

        rewardBalanceGallery.setAdapter(rewardBalanceGalleryAdapter);

        //get list view
        listView = (ListView)rootView.findViewById(R.id.list);

        customerActivityListViewAdapter = new CustomerActivityListViewAdapter(context, R.layout.content_of_customer_activity_list, customerActivityListResources);

        listView.setAdapter(customerActivityListViewAdapter);

        //Initialize pay with points button
        btnPayWithPoints = (Button) rootView.findViewById(R.id.btnPayWithPoints);

        btnPayWithPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveToPayWithPointsFragment();
            }
        });

        /*//get loyalty id from session
        SessionManager sessionManager = new SessionManager(context);
        UserInfoResource userInfoResource = sessionManager.getUserInfo();
        cusLoyaltyId = userInfoResource.getUsrLoginId();

        //set customer loyalty id
        lblCusLoyaltyId.setText(cusLoyaltyId);*/

    }

    /**
     * Method to get the customer reward balance
     */
    public void getCustomerRewardBalanceDetails(){

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {

            public void run() {

                try {

                    // Create the CustomerRewardBalanceService object
                    CustomerRewardBalanceService customerRewardBalanceService = new CustomerRewardBalanceService();

                    /// Hold the response
                    rewardBalanceResponse = customerRewardBalanceService.getRewardBalance(context, "14");


                } catch (Exception e) {

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);

                }

                // Post to the mUpdateCustomerSearch runnable
                mHandler.post(mUpdateCustomerRewardBalance);
            }
        };
        t.start();
    }

    /**
     * Method to update the customer reward balance
     */
    public void updateCustomerRewardBalanceResponse(){

        String status = rewardBalanceResponse.getStatus();

        if(status.equals("success")){

            //Get reward balance from response
            List<RewardBalanceResource> rewardBalanceResourceList = rewardBalanceResponse.getData();

            //Check whether reward balance list is null or size zero or not
            if(rewardBalanceResourceList!=null && rewardBalanceResourceList.size()>0){

                //add to reward balance list to reflect in spinner
                rewardBalanceResources.addAll(rewardBalanceResourceList);
            }

            rewardBalanceGalleryAdapter.notifyDataSetChanged();

            /*RewardBalanceResource rewardBalanceResource = rewardBalanceResourceList.get(0);

            //set reward points
            lblRewardPoints.setText(String.valueOf(rewardBalanceResource.getRwd_balance()));

            //set reward currency
            lblRewardCurrency.setText(rewardBalanceResource.getRwd_name());*/


        }else if (status.equals("failed")){

            String errorMessage = generalMethods.getTextualErrorString(context, rewardBalanceResponse.getErrorcode());

            //Show toast based on error code
            generalMethods.showToastMessage(context,errorMessage);
        }
    }

    /**
     * Method to get the customer activity list
     */
    public void getCustomerActivityDetails(){

        // get the Reference to the custom  progress dialog
        dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Customer", "fetching details...");

        // Show the dialog
        dlgProgress.show();

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {

            public void run() {

                try {

                    // Create the CustomerService object
                    CustomerService customerService = new CustomerService();

                    /// Hold the response
                    customerActivityListResponse = customerService.getCustomerActivityList(context, "14", "2016-01-31", "2016-12-31");


                } catch (Exception e) {

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);

                }

                // Post to the mUpdateCustomerSearch runnable
                mHandler.post(mUpdateCustomerActivityList);
            }
        };
        t.start();

    }

    /**
     * Method to update the response of the customer activity response
     * after the call api
     */
    public void updateCustomerActivityListResponse(){

        String status = customerActivityListResponse.getStatus();

        if(status.equals("success")){

            //Get customer activities from response
            List<CustomerActivityListResource> customerActivityListResourceList = customerActivityListResponse.getData();

            customerActivityListResources.addAll(customerActivityListResourceList);

            customerActivityListViewAdapter.notifyDataSetChanged();

            dlgProgress.dismiss();


        }else if (status.equals("failed")){

            String errorMessage = generalMethods.getTextualErrorString(context, customerActivityListResponse.getErrorcode());

            //Show toast based on error code
            generalMethods.showToastMessage(context,errorMessage);
        }
    }

    /**
     * Method to navigate the fragment to pay with points screen
     */
    public void moveToPayWithPointsFragment(){

        mListener.navigateToFragment(null, ApplicationWindows.PAY_WITH_POINTS);
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
        public void navigateToFragment(Bundle bundle,Integer uri);
    }
}
