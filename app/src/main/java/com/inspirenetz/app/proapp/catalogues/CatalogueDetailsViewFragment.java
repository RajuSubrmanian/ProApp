package com.inspirenetz.app.proapp.catalogues;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.dictionary.ApplicationWindows;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.CatalogueCartItemResource;
import com.inspirenetz.app.proapp.resources.CatalogueResource;
import com.inspirenetz.app.proapp.resources.UserInfoResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by raju on 2/22/17.
 */

public class CatalogueDetailsViewFragment extends Fragment {

    private View rootView;

    private Context context;

    private OnFragmentInteractionListener mListener;

    //Declare catalogue details
    private TextView txtCatProductDetails;
    private TextView txtCatCategoryName;
    private TextView txtCatMerchantName;
    private TextView txtCatRewardCurrency;
    private TextView txtPoints;
    private TextView lblCatalogueExpiryDate;

    //Declare cart count
    private TextView txtCartItemCount;

    //Declare cart image
    private ImageView icon_shopping_cart;

    private Button btnAddtoCart;

    //keep customer mobile number
    private String cusMobileNo = "";

    //keep cart items quantity
    private int cusTotalCartItems = 0;

    //keep current product no
    private String currentProductNo = "";

    //keep customer cart operation
    private String cusCartOperation = "";

    //check if product is exist
    private boolean isProductNoExist = false;

    //Declare recycler view
    private RecyclerView recyclerView;

    private CardView cardView;
    private ImageView imgCatalogueThumbnail;
    private TextView lblCatalogueName;

    private GeneralMethods generalMethods = new GeneralMethods();

    private SessionManager sessionManager;

    //create an object of CatalogueResource class
    CatalogueResource catalogueResourceObject = new CatalogueResource();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Save the context
        this.context = getActivity();

        // Create the session maanger object
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        // Check the login
        sessionManager.checkLogin();

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
        rootView= inflater.inflate(R.layout.fragment_view_catalogue_details, container, false);

        initialize();

        //Get Arguments
        Bundle params=getArguments();

        if(params!=null){

            setParams(params);
        }

        //call the method to get the cart size
        getTotalCartItemsCountByUser();

        return rootView;
    }

    /**
     * Method to initialize ui components
     */
    public void initialize(){

        //get customer mobile no
        UserInfoResource userInfoResource = sessionManager.getUserInfo();
        cusMobileNo = userInfoResource.getUsrLoginId();

        //initialize card view
        cardView = (CardView) rootView.findViewById(R.id.cardView);

        //initialize card image view
        imgCatalogueThumbnail = (ImageView)rootView.findViewById(R.id.imgCatalogueThumbnail);

        //initialize cart image view
        icon_shopping_cart = (ImageView)rootView.findViewById(R.id.icon_shopping_cart);

        //initialize card text view
        //lblCatalogueName = (TextView)rootView.findViewById(R.id.lblCatalogueName);

        //initialize text view's
        txtCatProductDetails = (TextView)rootView.findViewById(R.id.txtCatProductDetails);
        txtCatCategoryName = (TextView)rootView.findViewById(R.id.txtCatCategoryName);
        txtCatMerchantName = (TextView)rootView.findViewById(R.id.txtCatMerchantName);
        txtCatRewardCurrency = (TextView)rootView.findViewById(R.id.txtCatRewardCurrency);
        txtPoints = (TextView)rootView.findViewById(R.id.txtPoints);
        lblCatalogueExpiryDate = (TextView) rootView.findViewById(R.id.lblCatalogueExpiryDate);

        txtCartItemCount = (TextView)rootView.findViewById(R.id.txtCartItemCount);

        icon_shopping_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigateToCatalogueCartItemViewFragment();
            }
        });

        //initialize add to cart button
        btnAddtoCart = (Button)rootView.findViewById(R.id.btnAddtoCart);

        btnAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String buttonText = btnAddtoCart.getText().toString();

                if(buttonText.equals("ADD TO CART")){

                    addToCart();

                }else if (buttonText.equals("GO TO CART")){

                    navigateToCatalogueCartItemViewFragment();
                }

            }
        });

    }

    /**
     * Method to set Params
     * @param params pass bundle parameter
     */
    public void setParams(Bundle params){

        if(params.containsKey("catalogueDetails")) {

            CatalogueResource catalogueResource = (CatalogueResource) params.getSerializable("catalogueDetails");

            if (catalogueResource != null) {

                populateCatalogueDetails(catalogueResource);

            }
        }
    }

    /**
     * Method to show the total cart item count by user(loyalty id)
     */
    public void getTotalCartItemsCountByUser(){

        //get stored customer cart collection in hash map
        HashMap<String, List<CatalogueCartItemResource>> catalogueListResources = sessionManager.getCustomerCartCollection();

        //if hasp map list is null hide txtCartItemCount text view
        if(catalogueListResources == null){

            txtCartItemCount.setVisibility(View.GONE);

        }else {

            boolean isItemProcessed = false;

            //get cart collection by username(user login id) in list
            List<CatalogueCartItemResource> catalogueCartItemResources = catalogueListResources.get(cusMobileNo);

            if (catalogueCartItemResources!=null && catalogueCartItemResources.size()>0) {

                for (CatalogueCartItemResource catalogueCartItemResource : catalogueCartItemResources){

                    if(!catalogueCartItemResource.getStatus().equals("available")){

                        isItemProcessed = true;

                    }else if(currentProductNo.equals(catalogueCartItemResource.getCatProductNo())){

                        isProductNoExist = true;

                        break;
                    }

                }

                if (!isItemProcessed) {

                    cusTotalCartItems = catalogueCartItemResources.size();

                    //enable txtCartItemCount text view
                    txtCartItemCount.setVisibility(View.VISIBLE);

                    //set total cart items to view
                    txtCartItemCount.setText(String.valueOf(cusTotalCartItems));
                }
            }
        }

    }

    /**
     * Method to populate the catalogue
     * @param catalogueResource pass catalogue
     */
    public void populateCatalogueDetails(CatalogueResource catalogueResource){

        //store the values to CatalogueResource object
        catalogueResourceObject  = catalogueResource;

        //store current product no
        currentProductNo = catalogueResource.getCatProductNo();

        //set the values to the view
        String productDetails = catalogueResource.getCatProductCode()+" "+ catalogueResource.getCatDescription()
                +" "+ catalogueResource.getMerMerchantName()+" in "+ catalogueResource.getCatCategoryName();

        txtCatProductDetails.setText(productDetails);
        txtCatCategoryName.setText(catalogueResource.getCatCategoryName());
        txtCatMerchantName.setText(catalogueResource.getMerMerchantName());
        txtCatRewardCurrency.setText(catalogueResource.getRwdCurrencyName());
        //lblCatalogueName.setText(catalogueListResource.getCatDescription());
        txtPoints.setText(catalogueResource.getCatNumPoints());

        // Change expiry date format
        String newDateFormat = generalMethods.getDateFormat(context, catalogueResource.getCatEndDate());

        //set catalogue expiry date
        lblCatalogueExpiryDate.setText(newDateFormat);

        String catalogueImagePath = ApplicationConfiguration.MERCHANT_PROMOTIONS_IMAGE_PATH + catalogueResource.getCatProductImagePath();

        try{

            //loading catalogue images using glide library
            Glide.with(context).load(catalogueImagePath).into(imgCatalogueThumbnail);

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    /**
     * Method to call the addToCart method
     * pass customer mobile number and catalogueResourceObject as params
     */
    public void addToCart(){

        //call the method to add the customer catalogue items to the cart
        sessionManager.addCustomerCatalogueItemsToCart(cusMobileNo, catalogueResourceObject, "", "");

        if(!isProductNoExist){

            cusTotalCartItems = cusTotalCartItems+1;

            txtCartItemCount.setVisibility(View.VISIBLE);

            txtCartItemCount.setText(String.valueOf(cusTotalCartItems));

            generalMethods.showToastMessage(context, "Item Added to cart");
        }

        btnAddtoCart.setText("GO TO CART");

    }

    /**
     * Method to navigate the fragment to catalogue cart item view fragment
     */
    public void navigateToCatalogueCartItemViewFragment(){

        mListener.navigateToFragment(null, ApplicationWindows.CART_ITEMS_VIEW_SCREEN);
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
