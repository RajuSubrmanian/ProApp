package com.inspirenetz.app.proapp.promotions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.adapter.PromotionListRecyclerViewAdapter;
import com.inspirenetz.app.proapp.customer.CustomerHomePageTabsFragment;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.PromotionListResource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by raju on 2/4/17.
 */

public class PromotionDetailsViewFragment extends Fragment {

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
    private TextView lblShortDescription;

    //Declare Image button
    private ImageButton imgBtnClaimPromotion;

    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    GeneralMethods generalMethods=new GeneralMethods();

    private OnFragmentInteractionListener mListener;

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
        rootView= inflater.inflate(R.layout.fragment_promotion_details_view, container, false);

        initialize();


        //Get Arguments
        Bundle params=getArguments();

        if(params!=null){

            setParams(params);
        }

        return rootView;
    }

    /**
     * Method to initialize the ui componenets
     */
    public void initialize() {

        //initialize card view
        cardView = (CardView) rootView.findViewById(R.id.cardView);

        //initialize relative layout
        layPromotionDetailsContainer = (RelativeLayout) rootView.findViewById(R.id.layPromotionDetailsContainer);

        //Initialize promotion image view
        imgPromotionThumbnail = (ImageView)rootView.findViewById(R.id.imgPromotionThumbnail);
        lblPromotionName = (TextView)rootView.findViewById(R.id.lblPromotionName);
        lblPromotionExpiryDate = (TextView)rootView.findViewById(R.id.lblPromotionExpiryDate);
        lblPromotionMoreDetails = (TextView)rootView.findViewById(R.id.lblPromotionMoreDetails);
        lblShortDescription = (TextView)rootView.findViewById(R.id.lblShortDescription);

    }

    /**
     * Method to set Params
     * @param params
     */
    public void setParams(Bundle params){

        if(params.containsKey("promotionDetails")) {

            PromotionListResource promotionListResource = (PromotionListResource) params.getSerializable("promotionDetails");

            if (promotionListResource != null) {

                populatePromotionDetails(promotionListResource);

            }
        }
    }

    /**
     * Method to populate the promotion details
     * @param promotionListResource pass resource object
     */
    public void populatePromotionDetails(PromotionListResource promotionListResource){

        String prmName = promotionListResource.getPrmName();
        String imgPath = ApplicationConfiguration.MERCHANT_PROMOTIONS_IMAGE_PATH + promotionListResource.getPrmImagePath();
        String expiryDate = promotionListResource.getPrmExpiryDate();
        String longDescription = promotionListResource.getPrmLongDescription();
        String shortDescription = promotionListResource.getPrmShortDescription();

        try{

            //loading promotion images using glide library
            Glide.with(context).load(imgPath).into(imgPromotionThumbnail);

        }catch (Exception e){

            e.printStackTrace();
        }

        // Change expiry date format
        String newDateFormat = generalMethods.getDateFormat(context, expiryDate);

        lblPromotionName.setText(prmName);
        lblPromotionExpiryDate.setText(newDateFormat);
        lblPromotionMoreDetails.setText(longDescription);
        lblShortDescription.setText(shortDescription);
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
