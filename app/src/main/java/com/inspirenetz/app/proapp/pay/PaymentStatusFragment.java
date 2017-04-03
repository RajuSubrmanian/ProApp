package com.inspirenetz.app.proapp.pay;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;

/**
 * Created by Raju on 04-Oct-16.
 */
public class PaymentStatusFragment extends Fragment {

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    private SessionManager sessionManager;

    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    private GeneralMethods generalMethods = new GeneralMethods();

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
        if (!isConnected) {

            genMethod.showToastMessage(context, "Network Unavailable");

        }

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pay_with_points_status, container, false);

        // call initialize method
        initialize();

        // Get Arguments
        Bundle params = getArguments();

        if (params != null) {

            setArguments(params);

        }

        // Return root view
        return rootView;

    }

    /**
     * Method to initialize ui components
     */
    public void initialize(){

        // initialize ui components
        TextView txtTxnMerchantName = (TextView)rootView.findViewById(R.id.txtTxnMerchantName);
        TextView txtTxnCreditAcc = (TextView)rootView.findViewById(R.id.txtTxnCreditAcc);
        TextView txtTxnAmount = (TextView)rootView.findViewById(R.id.txtTxnAmount);
        TextView txtTxnReference = (TextView)rootView.findViewById(R.id.txtTxnReference);
        TextView txtTxnPaidWith = (TextView)rootView.findViewById(R.id.txtTxnPaidWith);
        TextView txtTxnPointsDebit = (TextView)rootView.findViewById(R.id.txtTxnPointsDebit);

        // set values to the ui


    }
}
