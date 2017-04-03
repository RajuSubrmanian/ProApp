package com.inspirenetz.app.proapp.promotions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.adapter.PromotionListRecyclerViewAdapter;
import com.inspirenetz.app.proapp.customer.CustomerHomePageTabsFragment;
import com.inspirenetz.app.proapp.dictionary.ApplicationWindows;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.PromotionListResource;
import com.inspirenetz.app.proapp.resources.PromotionListResponse;
import com.inspirenetz.app.proapp.service.PromotionService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raju on 2/3/17.
 */

public class PromotionListFragment extends Fragment {

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    //Declare recycler view
    private RecyclerView recyclerView;

    //Declare Promotion image view
    private CardView cardView;
    private ImageView imgPromotionThumbnail;
    private TextView lblPromotionName;

    //Declare promotion details
    private RelativeLayout layPromotionDetailsContainer;
    private ImageView imgFavouriteIcon;
    private TextView lblFavouriteCount;
    private TextView lblVlaidTill;
    private TextView lblPromotionExpiryDate;
    private TextView lblPromotionMoreDetails;

    //Declare Image button
    private ImageButton imgBtnClaimPromotion;

    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    GeneralMethods generalMethods=new GeneralMethods();

    private OnFragmentInteractionListener mListener;

    //create PromotionListResponse Object
    private PromotionListResponse promotionListResponse;

    //initialize array list for PromotionListResource class
    private ArrayList<PromotionListResource> promotionListResources = new ArrayList<PromotionListResource>();

    //create instance for PromotionListRecyclerViewAdapter class
    private PromotionListRecyclerViewAdapter promotionListRecyclerViewAdapter;

    //Runnable instance for mUpdatePromotionListResponse method
    final Runnable mUpdatePromotionListResponse = new Runnable() {

        @Override
        public void run() {

            updatePromotionListResponse();
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
        rootView= inflater.inflate(R.layout.fragment_promotions_list, container, false);

        initialize();

        //call method of getPromotionList
        getPromotionList();

        //Get Arguments
        Bundle params=getArguments();

        if(params!=null){

            //setParams(params);
        }

        return rootView;
    }

    /**
     * Method to initialize the ui componenets
     */
    public void initialize(){

        //initialize card view
        cardView = (CardView)rootView.findViewById(R.id.cardView);

        //initialize relative layout
        layPromotionDetailsContainer = (RelativeLayout)rootView.findViewById(R.id.layPromotionDetailsContainer);

        //call recycler view  adapter class
        promotionListRecyclerViewAdapter = new PromotionListRecyclerViewAdapter(context, promotionListResources, this);

        //initialize recycler view
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager =  new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(promotionListRecyclerViewAdapter);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Method to get promotion details
     */
    public void getPromotionList() {

        /*// get the Reference to the custom  progress dialog
        dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Promotion", "fetching details...");

        // Show the dialog
        dlgProgress.show();*/

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread

        Thread thread = new Thread() {

            public void run() {

                try {

                    PromotionService membershipService = new PromotionService();

                    promotionListResponse = membershipService.getPromotions(context);

                } catch (Exception e)
 {

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                }

                mHandler.post(mUpdatePromotionListResponse);
            }
        };
        thread.start();
    }

    /**
     * Method to get response and update
     * the promotion list in the recycler view
     */
    public void updatePromotionListResponse() {

        // Get the status
        String status = promotionListResponse.getStatus();

        if (status.equals("success")) {

            List<PromotionListResource> promotionListResourceList = promotionListResponse.getData();

            /*SessionManager sessionManager = new SessionManager(context);

            sessionManager.createMembershipDetailsSession(membershipResourceList);*/

            promotionListResources.addAll(promotionListResourceList);

            promotionListRecyclerViewAdapter.notifyDataSetChanged();

            //Dismiss dialog
            //dlgProgress.dismiss();

        } else if (status.equals("failed")) {

            String errorMessage = generalMethods.getTextualErrorString(context, promotionListResponse.getErrorcode());

            //Show toast based on error code
            generalMethods.showToastMessage(context,errorMessage);

        }
    }

    /**
     * Method to move to fragment promotion details view
     * @param promotionListResource1 pass resource object
     */
    public void moveToFragmentPromotionDetailsView(PromotionListResource promotionListResource1){

        Bundle bundle = new Bundle();
        bundle.putSerializable("promotionDetails",promotionListResource1);

        mListener.navigateToFragment(bundle, ApplicationWindows.PROMOTION_DETAILS_VIEW_SCREEN);

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
