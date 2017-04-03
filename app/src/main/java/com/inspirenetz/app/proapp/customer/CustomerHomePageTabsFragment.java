package com.inspirenetz.app.proapp.customer;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.adapter.CustomerHomePagerAdapter;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;

/**
 * Created by raju on 2/2/17.
 */

public class CustomerHomePageTabsFragment extends android.support.v4.app.Fragment {

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    private OnFragmentInteractionListener mListener;

    //declare customer home page adapter
    private CustomerHomePagerAdapter customerHomePagerAdapter;

    //declare customer home page tab
    private TabLayout customerHomePageTab;

    //declare customer home view pager
    private ViewPager customerHomePager;

    //keep mobile number
    private String mobileNo = "";

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
        rootView= inflater.inflate(R.layout.fragment_customer_home_page_tabs, container, false);

        initialize();

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

        //get customer home page tab
        customerHomePageTab = (TabLayout)rootView.findViewById(R.id.customerHomePageTab);

        //add text to the tabs
        customerHomePageTab.addTab(customerHomePageTab.newTab().setText("HOME"));
        customerHomePageTab.addTab(customerHomePageTab.newTab().setText("PROMOTIONS"));
        customerHomePageTab.setTabGravity(TabLayout.GRAVITY_FILL);

        //get customer home page adapter
        customerHomePagerAdapter = new CustomerHomePagerAdapter(getChildFragmentManager(),customerHomePageTab.getTabCount());

        //get customer view pager
        customerHomePager = (ViewPager)rootView.findViewById(R.id.customerHomePager);

        customerHomePager.setAdapter(customerHomePagerAdapter);

        customerHomePager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(customerHomePageTab));

        customerHomePageTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                customerHomePager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
