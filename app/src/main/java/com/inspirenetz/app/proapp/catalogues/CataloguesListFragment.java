package com.inspirenetz.app.proapp.catalogues;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inspirenetz.app.proapp.CatalogueSearchActivity;
import com.inspirenetz.app.proapp.MainActivity;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.adapter.CatalogueCategoryRecyclerViewAdapter;
import com.inspirenetz.app.proapp.adapter.CatalogueListRecyclerViewAdapter;
import com.inspirenetz.app.proapp.dictionary.ApplicationWindows;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.promotions.GridSpacingItemDecoration;
import com.inspirenetz.app.proapp.resources.CatalogueCartItemResource;
import com.inspirenetz.app.proapp.resources.CatalogueResource;
import com.inspirenetz.app.proapp.resources.CatalogueResponse;
import com.inspirenetz.app.proapp.resources.UserInfoResource;
import com.inspirenetz.app.proapp.service.CatalogueService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by raju on 2/15/17.
 */

public class CataloguesListFragment extends Fragment {

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    //declare search box relative layout
    private RelativeLayout laySearchBoxContainer;

    private RelativeLayout layCatalogueRecyclerViewContainer;

    private RelativeLayout layCatalogueCategoriesContainer;

    //Declare cart count
    private TextView txtCartItemCount;

    private TextView lblCatCategoryName;

    private ImageView imgSearchIcon;

    public EditText txtSearchItems;

    //Declare cart image
    private ImageView icon_shopping_cart;

    //Declaer three dots action
    private ImageView imgThreeDots;

    //keep customer mobile number
    private String cusMobileNo = "";

    //keep catalogue category
    private String catCategory = "";

    //keep category name
    private String catCategoryName = "";

    //keep cart items quantity
    private int cusTotalCartItems = 0;

    private CardView cardView;

    private RecyclerView recyclerView;

    //recycler view for catalogue category
    private RecyclerView recyclerViewOfCatalogueCategory;

    private CatalogueListRecyclerViewAdapter catalogueListRecyclerViewAdapter;

    final Handler mHandler = new Handler();

    private GeneralMethods generalMethods = new GeneralMethods();

    private Dialog dlgProgress;

    private SessionManager sessionManager;

    private OnFragmentInteractionListener mListener;

    private CatalogueResponse catalogueResponse;

    private ArrayList<CatalogueResource> catalogueResources = new ArrayList<CatalogueResource>();

    private CatalogueCategoryRecyclerViewAdapter catalogueCategoryRecyclerViewAdapter;

    //Runnable instance for mUpdateCatalogueList method
    final Runnable mUpdateCatalogueList = new Runnable() {

        @Override
        public void run() {

            updateCatalogueListResponse();
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
        rootView= inflater.inflate(R.layout.fragment_catalogue_list, container, false);

        initialize();

        //get catalogue list
        getCatalogueList();

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

        //get customer mobile no
        UserInfoResource userInfoResource = sessionManager.getUserInfo();
        cusMobileNo = userInfoResource.getUsrLoginId();

        imgSearchIcon = (ImageView)rootView.findViewById(R.id.imgSearchIcon);

        txtSearchItems = (EditText)rootView.findViewById(R.id.txtSearchItems);

        laySearchBoxContainer = (RelativeLayout)rootView.findViewById(R.id.laySearchBoxContainer);

        layCatalogueRecyclerViewContainer = (RelativeLayout)rootView.findViewById(R.id.layCatalogueRecyclerViewContainer);

        layCatalogueCategoriesContainer = (RelativeLayout)rootView.findViewById(R.id.layCatalogueCategoriesContainer);

        //initialize cart image view
        icon_shopping_cart = (ImageView)rootView.findViewById(R.id.icon_shopping_cart);

        //initialize three dots image view
        imgThreeDots = (ImageView)rootView.findViewById(R.id.imgThreeDots);

        //initialize cart item count
        txtCartItemCount = (TextView)rootView.findViewById(R.id.txtCartItemCount);

        //initialize catalogue category name
        lblCatCategoryName = (TextView)rootView.findViewById(R.id.lblCatCategoryName);

        cardView = (CardView)rootView.findViewById(R.id.cardView);

        //call recycler view  adapter class
        catalogueListRecyclerViewAdapter = new CatalogueListRecyclerViewAdapter(context, R.id.lblCatalogueName, catalogueResources, this);

        //initialize recycler view
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager =  new GridLayoutManager(context, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(catalogueListRecyclerViewAdapter);

        recyclerViewOfCatalogueCategory  = (RecyclerView)rootView.findViewById(R.id.recyclerViewOfCatalogueCategory);

        addTextListener();

        txtSearchItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgSearchIcon.setVisibility(View.GONE);
                txtSearchItems.setFocusableInTouchMode(true);
            }
        });

        icon_shopping_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigateToCatalogueCartItemViewFragment();
            }
        });

        laySearchBoxContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showCatalogueSearchActivity();

                imgSearchIcon.setVisibility(View.GONE);
                txtSearchItems.setFocusableInTouchMode(true);
            }
        });

        imgThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCataloguePopupWindow(view);

            }
        });

        catCategory = "0";

        getTotalCartItemsCountByUser();
    }

    /**
     * Method to clear the catalogue list initially
     * before its loaded into recycler view
     */
    public void clearCatalogues(){

        catalogueResources.clear();

        catalogueListRecyclerViewAdapter.notifyDataSetChanged();

    }

    public void addTextListener(){

        txtSearchItems.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                //catalogueListRecyclerViewAdapter.getFilter().filter(query.toString());

                query = query.toString().toLowerCase();

                final List<CatalogueResource> filteredList = new ArrayList<CatalogueResource>();

                for(CatalogueResource catalogueResource : catalogueResources){

                    if(catalogueResource.getCatDescription().toLowerCase().contains(query)){

                        filteredList.add(catalogueResource);
                    }
                }

                //call recycler view  adapter class
                catalogueListRecyclerViewAdapter = new CatalogueListRecyclerViewAdapter(context, R.id.lblCatalogueName, filteredList, CataloguesListFragment.this);

                //initialize recycler view
                recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);

                RecyclerView.LayoutManager layoutManager =  new GridLayoutManager(context, 2);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(catalogueListRecyclerViewAdapter);

                catalogueListRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Method to show popup for catalogue categories
     * when click the three dots icon
     */
    public void showCataloguePopupWindow(View view){

        //inflate menu
        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        final PopupMenu popup = new PopupMenu(wrapper, view, Gravity.START);
        final MenuInflater menuInflaterater = popup.getMenuInflater();
        menuInflaterater.inflate(R.menu.catalogue_categories, popup.getMenu());

        // show popup menu with icon
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int id = menuItem.getItemId();

                if(id == R.id.action_catalogue_category){

                    showCatalogueCategories();

                }

                return true;
            }
        });
    }

    /**
     * Method to show the catalogue search activity
     * when click the search box
     */
    public void showCatalogueSearchActivity(){

        /*Intent intent = new Intent(context, CatalogueSearchActivity.class);

        startActivity(intent);*/
    }

    /**
     * Method to show the catalogue categories
     * when the use clicks the category option
     */
    public void showCatalogueCategories(){

        laySearchBoxContainer.setVisibility(View.GONE);
        layCatalogueRecyclerViewContainer.setVisibility(View.GONE);
        lblCatCategoryName.setVisibility(View.GONE);
        layCatalogueCategoriesContainer.setVisibility(View.VISIBLE);

        List<String> catCategoryList = new ArrayList<String>();
        catCategoryList.add("Dining");
        catCategoryList.add("Electronic and Gadgets");
        catCategoryList.add("Food and Beverages");
        catCategoryList.add("Health and beauty");
        catCategoryList.add("Leisure and Entertainment");
        catCategoryList.add("Travel and Holidays");
        catCategoryList.add("Shopping");

        catalogueCategoryRecyclerViewAdapter = new CatalogueCategoryRecyclerViewAdapter(context, catCategoryList, this);

        RecyclerView.LayoutManager layoutManager =  new GridLayoutManager(context, 2);
        recyclerViewOfCatalogueCategory.setHasFixedSize(true);
        recyclerViewOfCatalogueCategory.setLayoutManager(layoutManager);
        //recyclerViewOfCatalogueCategory.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewOfCatalogueCategory.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOfCatalogueCategory.setAdapter(catalogueCategoryRecyclerViewAdapter);

    }

    /**
     * Method to show the total cart item count by user(loyalty id)
     */
    public void getTotalCartItemsCountByUser(){

        boolean isItemProcessed = false;

        //get stored customer cart collection in hash map
        HashMap<String, List<CatalogueCartItemResource>> catalogueListResources = sessionManager.getCustomerCartCollection();

        //if hasp map list is null hide txtCartItemCount text view
        if(catalogueListResources == null){

            txtCartItemCount.setVisibility(View.GONE);

        }else {

            //get cart collection by username(user login id) in list
            List<CatalogueCartItemResource> catalogueCartItemResources = catalogueListResources.get(cusMobileNo);

            if (catalogueCartItemResources!=null && catalogueCartItemResources.size()>0) {

                for (CatalogueCartItemResource catalogueCartItemResource : catalogueCartItemResources){

                    if(!catalogueCartItemResource.getStatus().equals("available")){

                        isItemProcessed = true;
                    }
                }

                if (!isItemProcessed){

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
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Method to get catalogue list items
     */
    public void getCatalogueList(){

        // get the Reference to the custom  progress dialog
        dlgProgress = new GeneralMethods().getCustomPogressDialog(context, "Catalogue", "fetching details...");

        // Show the dialog
        dlgProgress.show();

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {

            public void run() {

                try {

                    // Create the CatalogueService object
                    CatalogueService catalogueService = new CatalogueService();

                    /// Hold the response
                    catalogueResponse = catalogueService.getCatalogueList(context, "name", "", "14", catCategory, "1", "2");


                } catch (Exception e) {

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);

                }

                // Post to the mUpdateCustomerSearch runnable
                mHandler.post(mUpdateCatalogueList);
            }
        };
        t.start();
    }

    /**
     * Method to update the catalogue list response
     * after call the api
     */
    public void updateCatalogueListResponse(){

        clearCatalogues();

        //Dismiss dialog
        dlgProgress.dismiss();

        // Get the status
        String status = catalogueResponse.getStatus();

        if (status.equals("success")) {

            List<CatalogueResource> catalogueListResource = catalogueResponse.getData();

            if(catalogueListResource.size()==0){

                generalMethods.showToastMessage(context, "There is no catalogues");

            }else if (catalogueListResource.size()>0){

                laySearchBoxContainer.setVisibility(View.VISIBLE);
                layCatalogueRecyclerViewContainer.setVisibility(View.VISIBLE);
                layCatalogueCategoriesContainer.setVisibility(View.GONE);

                if(!catCategoryName.equals("")){

                    //enable category name text view after select the category
                    lblCatCategoryName.setVisibility(View.VISIBLE);

                    //set category name
                    lblCatCategoryName.setText(catCategoryName);
                }

                catalogueResources.addAll(catalogueListResource);

                catalogueListRecyclerViewAdapter.notifyDataSetChanged();
            }

        } else if (status.equals("failed")) {

            String errorMessage = generalMethods.getTextualErrorString(context, catalogueResponse.getErrorcode());

            //Show toast based on error code
            generalMethods.showToastMessage(context,errorMessage);

        }
    }

    /**
     * Method to get the catalogue list basad on the selected category
     */
    public void getCatalogueListByCategory(int catCategoryType, String categoryName){

        //store category type
        catCategory = String.valueOf(catCategoryType);

        //store category name
        catCategoryName = categoryName;

        //call the method to get catalogue as per catalogue category
        getCatalogueList();
    }

    /**
     * Method to move to fragment catalogue details view
     * @param catalogueResource pass resource object
     */
    public void moveToFragmentCatalogueDetailsView(CatalogueResource catalogueResource){

        Bundle bundle = new Bundle();
        bundle.putSerializable("catalogueDetails", catalogueResource);

        mListener.navigateToFragment(bundle, ApplicationWindows.CATALOGUES_DETAILS_VIEW_SCREEN);

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
            mListener = (CataloguesListFragment.OnFragmentInteractionListener) activity;
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
