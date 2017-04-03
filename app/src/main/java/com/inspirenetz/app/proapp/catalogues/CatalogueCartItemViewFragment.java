package com.inspirenetz.app.proapp.catalogues;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.adapter.CartItemListAdapter;
import com.inspirenetz.app.proapp.dictionary.ApplicationWindows;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.CatalogueCartItemResource;
import com.inspirenetz.app.proapp.resources.CatalogueRedemptionResponse;
import com.inspirenetz.app.proapp.resources.CatalogueResource;
import com.inspirenetz.app.proapp.resources.UserInfoResource;
import com.inspirenetz.app.proapp.service.CatalogueService;
import com.inspirenetz.app.proapp.service.PayService;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by raju on 2/28/17.
 */

public class CatalogueCartItemViewFragment extends Fragment{

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    private OnFragmentInteractionListener mListener;

    //declare list view container layout
    private RelativeLayout layCartListViewContainer;

    //declare bottom container
    private RelativeLayout layBottomContainer;

    //declare empty cart container
    private RelativeLayout layEmptyCartContainer;

    //Declare list view
    private ListView listView;

    private Button btnRedemptionContinue;

    private Button btnAddItem;

    private TextView lblTotalPointsUsed;

    //Declare total points used for purchase text view
    private TextView txtTotalPointsUsedForPurchase;

    //keep loyalty id
    private String loyaltyId = "";

    //keep item quantity
    private int cartItemQuantity = 0;

    //keep points required for the item
    private int itemRequiredPoints = 0;

    //kee total points used for purchase
    private int totalPointsUsedForPurchase = 0;

    private Dialog dlgProgress;

    final Handler mHandler = new Handler();

    private SessionManager sessionManager;

    private GeneralMethods generalMethods = new GeneralMethods();

    private CartItemListAdapter cartItemListAdapter;

    private List<CatalogueCartItemResource> catalogueCartItemResourceArrayList = new ArrayList<CatalogueCartItemResource>();

    private CatalogueRedemptionResponse catalogueRedemptionResponse;

    private CatalogueResource catalogueResource;

    //Runnable instance for mPostCatalogueRedemption method
    final Runnable mPostCatalogueRedemption = new Runnable() {

        @Override
        public void run() {

            updateCatalogueRedemptionResponse();
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
        rootView= inflater.inflate(R.layout.fragment_cart_items_list, container, false);

        initialize();

        //call the method to show the customer cart collections
        showCustomerCartCollections();


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

        //get the customer loyalty id from the session
        UserInfoResource userInfoResource = sessionManager.getUserInfo();
        loyaltyId = userInfoResource.getUsrLoginId();

        //get cart list view container layout
        layCartListViewContainer = (RelativeLayout)rootView.findViewById(R.id.layCartListViewContainer);

        //get cart items view bottom container layout
        layBottomContainer = (RelativeLayout)rootView.findViewById(R.id.layBottomContainer);

        //get empty cart container layout
        layEmptyCartContainer = (RelativeLayout)rootView.findViewById(R.id.layEmptyCartContainer);

        //get the label of total points used
        lblTotalPointsUsed = (TextView)rootView.findViewById(R.id.lblTotalPointsUsed);

        //get total points used for purchase text view
        txtTotalPointsUsedForPurchase = (TextView)rootView.findViewById(R.id.txtTotalPointsUsedForPurchase);

        //get checkout cart button
        btnRedemptionContinue = (Button)rootView.findViewById(R.id.btnRedemptionContinue);

        //get add item button
        btnAddItem = (Button)rootView.findViewById(R.id.btnAddItem);

        //get list view
        listView = (ListView)rootView.findViewById(R.id.list);

        cartItemListAdapter = new CartItemListAdapter(context, R.layout.content_of_cart_items_list, catalogueCartItemResourceArrayList, this);

        listView.setAdapter(cartItemListAdapter);

        btnRedemptionContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String buttonText = btnRedemptionContinue.getText().toString();

                if(buttonText.equals("BACK TO CART")){

                    showEmptyCartView();

                }else {

                    postCatalogueRedemption();
                }
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigateToCatalogueListFragment();
            }
        });
    }

    /**
     * Method to clear the cart item initially
     * in the list
     */
    public void clearCartItems(){

        totalPointsUsedForPurchase = 0;

        catalogueCartItemResourceArrayList.clear();

        cartItemListAdapter.notifyDataSetChanged();

    }

    /**
     * Method to show the customer cart collection details
     */
    public void showCustomerCartCollections(){

        boolean isItemProcessed = false;

        clearCartItems();

        //get stored customer cart collection in hash map
        HashMap<String, List<CatalogueCartItemResource>> catalogueListResources = sessionManager.getCustomerCartCollection();

        //get cart collection by username(user login id) in list
        List<CatalogueCartItemResource> catalogueCartItemResources = catalogueListResources.get(loyaltyId);

        //check cart is empty
        if(catalogueCartItemResources.size()==0 && catalogueCartItemResources.isEmpty()){

            //hide cart list view container
            layCartListViewContainer.setVisibility(View.GONE);

            //hide cart items bottom container
            layBottomContainer.setVisibility(View.GONE);

            //show empty cart container
            layEmptyCartContainer.setVisibility(View.VISIBLE);


        }else if (catalogueCartItemResources.size()>0 && !catalogueCartItemResources.isEmpty()){

            ///hide empty cart container
            layEmptyCartContainer.setVisibility(View.GONE);

            //show cart list view container
            layCartListViewContainer.setVisibility(View.VISIBLE);

            //show cart item bottom container
            layBottomContainer.setVisibility(View.VISIBLE);

            for (CatalogueCartItemResource catalogueCartItemResource : catalogueCartItemResources){

                if(!catalogueCartItemResource.getStatus().equals("available")){

                    isItemProcessed = true;
                }

                catalogueResource = catalogueCartItemResource.getCatalogueResource();

                //get the cart item quantity
                cartItemQuantity = catalogueCartItemResource.getQty();

                //get item required points
                itemRequiredPoints = Integer.parseInt(catalogueResource.getCatNumPoints());

                int totalPoints = cartItemQuantity * itemRequiredPoints;

                totalPointsUsedForPurchase = totalPoints + totalPointsUsedForPurchase;

            }

            if (isItemProcessed){

                changeBottomLayoutOfCart();
            }

            txtTotalPointsUsedForPurchase.setText(String.valueOf(totalPointsUsedForPurchase));

            //add cart item list to array adapter
            catalogueCartItemResourceArrayList.addAll(catalogueCartItemResources);

            cartItemListAdapter.notifyDataSetChanged();
        }

    }

    /**
     * Method to decrease the item quantity in cart
     * @param cusLoyaltyId customer mobile no
     * @param catalogueResource catalogue resource class
     * @param itemRequiredPoints item required points
     */
    public void decreaseCartItemQuantity(String cusLoyaltyId, CatalogueResource catalogueResource, int itemRequiredPoints){

        sessionManager.addCustomerCatalogueItemsToCart(cusLoyaltyId, catalogueResource,"decreaseQuantity", "");

        totalPointsUsedForPurchase = totalPointsUsedForPurchase - itemRequiredPoints;

        txtTotalPointsUsedForPurchase.setText(String.valueOf(totalPointsUsedForPurchase));

    }

    /**
     * Method to increase the item quantity in cart
     * @param cusLoyaltyId customer mobile no
     * @param catalogueResource catalogue resource class
     * @param itemRequiredPoints item required points
     */
    public void increaseCartItemQuantity(String cusLoyaltyId, CatalogueResource catalogueResource, int itemRequiredPoints){

        sessionManager.addCustomerCatalogueItemsToCart(cusLoyaltyId, catalogueResource, "", "");

        totalPointsUsedForPurchase = totalPointsUsedForPurchase + itemRequiredPoints;

        txtTotalPointsUsedForPurchase.setText(String.valueOf(totalPointsUsedForPurchase));
    }

    /**
     * Method to remove the clicked item from the cart
     * @param cusLoyaltyId customer mobile number
     * @param catalogueResource catalogue resource  class object
     * @param buttonAction button action from the cart list
     */
    public void removeCartItem(String cusLoyaltyId, CatalogueResource catalogueResource, String buttonAction, String itemRequiredPoints){

        //call the method in shared preferences to remove the item in cart list
        sessionManager.addCustomerCatalogueItemsToCart(cusLoyaltyId, catalogueResource,buttonAction,"");

        showCustomerCartCollections();
    }

    /**
     * Method to check out the cart items
     */
    public void checkoutCartItems(){

        /*//get stored customer cart collection in hash map
        HashMap<String, List<CatalogueCartItemResource>> catalogueListResources = sessionManager.getCustomerCartCollection();

        //get cart collection by username(user login id) in list
        List<CatalogueCartItemResource> catalogueCartItemResources = catalogueListResources.get(loyaltyId);

        for(CatalogueCartItemResource catalogueCartItemResource : catalogueCartItemResources){

            catalogueCartItemResource.setStatus("checkout");
        }*/

        /*sessionManager.addCustomerCatalogueItemsToCart(loyaltyId, catalogueResource, "checkoutCart");

        showCustomerCartCollections();*/
    }

    /**
     * Method to show the empty cart
     */
    public void showEmptyCartView(){

        //sessionManager.addCustomerCatalogueItemsToCart(loyaltyId, catalogueResource, "removeItem", "");

        clearCartItems();

        //get stored customer cart collection in hash map
        HashMap<String, List<CatalogueCartItemResource>> catalogueListResources = sessionManager.getCustomerCartCollection();

        //get cart collection by username(user login id) in list
        List<CatalogueCartItemResource> catalogueCartItemResources = catalogueListResources.get(loyaltyId);

        catalogueCartItemResources.clear();

        //put the values to hash map (customer, items)
        catalogueListResources.put(loyaltyId, catalogueCartItemResources);

        //call the method and pass the hash map values to store in shared preference
        sessionManager.createOrUpdateCustomerCartCollection(catalogueListResources);

        //hide cart list view container
        layCartListViewContainer.setVisibility(View.GONE);

        //hide cart items bottom container
        layBottomContainer.setVisibility(View.GONE);

        //show empty cart container
        layEmptyCartContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Method to post catalogue redemption to server
     * for all available item in the cart
     */
    public void postCatalogueRedemption() {

        // get the Reference to the custom  progress dialog
        dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Redemption", "in process...");

        // Show the dialog
        dlgProgress.show();

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread

        Thread thread = new Thread() {

            public void run() {

                try {

                    //get stored customer cart collection in hash map
                    HashMap<String, List<CatalogueCartItemResource>> catalogueListResources = sessionManager.getCustomerCartCollection();

                    //get cart collection by username(user login id) in list
                    List<CatalogueCartItemResource> catalogueCartItemResources = catalogueListResources.get(loyaltyId);

                    for(CatalogueCartItemResource catalogueCartItemResource : catalogueCartItemResources){

                        CatalogueResource catalogueResource = catalogueCartItemResource.getCatalogueResource();

                        String productCode = catalogueResource.getCatProductCode();

                        String merchantNo = catalogueResource.getCatMerchantNo();

                        int itemQuantity = catalogueCartItemResource.getQty();

                        CatalogueService catalogueService = new CatalogueService();

                        catalogueRedemptionResponse = catalogueService.postCatalogueRedemption(context, productCode, itemQuantity, "1","0", merchantNo, loyaltyId, catalogueResource);
                    }

                } catch (Exception e) {

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                }

                mHandler.post(mPostCatalogueRedemption);
            }
        };
        thread.start();
    }

    /**
     * Method to get response and update
     * the response of post pay bill
     */
    public void updateCatalogueRedemptionResponse() {

        //Dismiss dialog
        dlgProgress.dismiss();

        //changeBottomLayoutOfCart();

        showCustomerCartCollections();

        /*//showCustomerCartCollections();

        // Get the status
        String status = catalogueRedemptionResponse.getStatus();

        if (status.equals("success")) {

            generalMethods.showToastMessage(context, "Payment success");

            //populatePaymentStatus();

        } else if (status.equals("failed")) {

            String errorMessage = generalMethods.getTextualErrorString(context, catalogueRedemptionResponse.getErrorcode());

            //Show toast based on error code
            generalMethods.showToastMessage(context,errorMessage);

        }*/
    }

    /**
     * Method to change bottom layout of cart
     */
    public void changeBottomLayoutOfCart(){

        //disable bottom layout labels of cart item
        lblTotalPointsUsed.setVisibility(View.GONE);
        txtTotalPointsUsedForPurchase.setVisibility(View.GONE);

        //change the button after processed the redemption
        btnRedemptionContinue.setText("BACK TO CART");
    }

    /**
     * method to navigate the fragment to catalogue list fragment
     * when click the add item button in the empty cart
     */
    public void navigateToCatalogueListFragment(){

        mListener.navigateToFragment(null, ApplicationWindows.CATALOGUES_SCREEN);
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
