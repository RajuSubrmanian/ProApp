package com.inspirenetz.app.proapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.inspirenetz.app.proapp.adapter.CustomerHomePagerAdapter;
import com.inspirenetz.app.proapp.catalogues.CatalogueCartItemViewFragment;
import com.inspirenetz.app.proapp.catalogues.CatalogueDetailsViewFragment;
import com.inspirenetz.app.proapp.catalogues.CataloguesListFragment;
import com.inspirenetz.app.proapp.customer.CustomerHomePageDetailsFragment;
import com.inspirenetz.app.proapp.customer.CustomerHomePageTabsFragment;
import com.inspirenetz.app.proapp.customer.CustomerProfileDetailsFragment;
import com.inspirenetz.app.proapp.customer.ShowCustomerQRCodeFragment;
import com.inspirenetz.app.proapp.dictionary.ApplicationWindows;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.pay.PayMerchantBillsFragment;
import com.inspirenetz.app.proapp.pay.PayWithPointsFragment;
import com.inspirenetz.app.proapp.promotions.PromotionDetailsViewFragment;
import com.inspirenetz.app.proapp.promotions.PromotionListFragment;
import com.inspirenetz.app.proapp.resources.UserInfoResource;
import com.inspirenetz.app.proapp.util.RoundedImageView;

import java.util.Stack;

public class CustomerHomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,CustomerHomePageTabsFragment.OnFragmentInteractionListener
        ,PayWithPointsFragment.OnFragmentInteractionListener,PromotionListFragment.OnFragmentInteractionListener
        ,PromotionDetailsViewFragment.OnFragmentInteractionListener,CustomerHomePageDetailsFragment.OnFragmentInteractionListener
        ,CataloguesListFragment.OnFragmentInteractionListener, CatalogueDetailsViewFragment.OnFragmentInteractionListener
        ,CatalogueCartItemViewFragment.OnFragmentInteractionListener{

    // Declare context
    private Context context;

    // Declare Relative layout
    private RelativeLayout layAppTitleBar;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;

    //initialize app bar title icon
    private ImageView titleBarIcon;

    //initialize app bar title Text
    private TextView titleBarTitle;

    //initialize app bar title logo
    private ImageView titleBarLogo;

    //initialize search icon
    private ImageView imgSearchIcon;
    private ImageView imgShoppingIcon;

    private UserInfoResource userInfoResource;

    //initialize session manager object
    SessionManager sessionManager;

    //declare customer home page adapter
    private CustomerHomePagerAdapter customerHomePagerAdapter;

    //declare customer home page tab
    private TabLayout customerHomePageTab;

    //declare customer home view pager
    private ViewPager customerHomePager;

    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    private Stack<Integer> fragmentStack;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Make full screen app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get context
        context =  CustomerHomeActivity.this;

        // Get fragment manager
        fragmentManager = getSupportFragmentManager();

        // Get session manager
        sessionManager = new SessionManager(context);

        // Check the login
        boolean isLoggedIn = sessionManager.isLoggedIn();

        // If there is no login then we need to call
        // checkLogin that will show the LoginActivity and
        // then call the finish() on this activity
        if ( !isLoggedIn ) {

            // call the checkLogin()
            sessionManager.checkLogin();

            // IMPORTANT: This call is required so that the
            // customerpage activity will be finished and user
            // can't click on the 'Back' from the login activity that
            // loaded before.
            finish();
        }

        setContentView(R.layout.activity_customer_home);

        userInfoResource = sessionManager.getUserInfo();

        //Initialize fragment stack
        fragmentStack=new Stack<Integer>();

        // Get navigation view
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //set naviagtion view onitem selected listener
        navigationView.setNavigationItemSelectedListener(this);

        //initialize navigation drawer
        initializeNavigationDrawer(navigationView);

        //initialize UI elements
        initialize();

        //call the landing page
        landingPageWindow();

        //enable camera permission
        //enableCameraPermission();

    }

    /**
     * Method to show the landing page
     */
    protected void landingPageWindow(){

        navigateToFragment(null, ApplicationWindows.CUSTOMER_HOME_PAGE);
    }

    /**
     * Method to initialize ui components
     */
    protected void initialize(){

        //get app title bar
        layAppTitleBar=(RelativeLayout)findViewById(R.id.layAppTitleBar);

        //get app title icon
        titleBarIcon=(ImageView)findViewById(R.id.titleBarIcon);

        //set onclick listener
        titleBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate_humberger_icon);
                titleBarIcon.startAnimation(animation);*/

                openNavigationDrawer();

            }
        });

        //get app title text
        titleBarTitle=(TextView)findViewById(R.id.titleBarTitle);

        //get app title logo
        titleBarLogo=(ImageView)findViewById(R.id.titleBarLogo);

        //get shopping image icon
        imgShoppingIcon=(ImageView)findViewById(R.id.imgShoppingIcon);

        //get search icon
        imgSearchIcon=(ImageView)findViewById(R.id.imgSearchIcon);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(fragmentStack.size() > 1){

                fragmentStack.lastElement();

                /*Bundle bundle=null;

                if(fragmentStack.lastElement()==ApplicationWindows.CATALOGUES_DETAILS_VIEW_SCREEN){

                    CatalogueDetailsViewFragment catalogueDetailsViewFragment=(CatalogueDetailsViewFragment)getSupportFragmentManager().findFragmentByTag(String.valueOf(ApplicationWindows.CATALOGUES_DETAILS_VIEW_SCREEN));

                    if (catalogueDetailsViewFragment != null) {
                        // If article frag is available, we're in two-pane layout...

                        // Call a method in the ChargeCardViewFragment to update data
                        bundle=catalogueDetailsViewFragment.getArguments();
                    }
                }*/

                fragmentStack.pop();

                if(fragmentStack.lastElement()== ApplicationWindows.CUSTOMER_HOME_PAGE){

                    navigateToFragment(null, ApplicationWindows.CUSTOMER_HOME_PAGE);

                }else if(fragmentStack.lastElement()== ApplicationWindows.CATALOGUES_SCREEN){

                    navigateToFragment(null, ApplicationWindows.CATALOGUES_SCREEN);

                }

            }else if(fragmentStack.size()>0 && !(fragmentStack.lastElement()== ApplicationWindows.CUSTOMER_HOME_PAGE)){

                navigateToFragment(null, ApplicationWindows.CUSTOMER_HOME_PAGE);

            }else{

                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            navigateToFragment(null, ApplicationWindows.CUSTOMER_HOME_PAGE);

        } else if (id == R.id.nav_pay_with_points) {

            navigateToFragment(null, ApplicationWindows.PAY_WITH_POINTS);

        } else if (id == R.id.nav_my_profile) {

            navigateToFragment(null, ApplicationWindows.CUSTOMER_PROFILE_SCREEN);

        }else if (id == R.id.nav_catalogues) {

            navigateToFragment(null, ApplicationWindows.CATALOGUES_SCREEN);

        }else if (id == R.id.nav_show_qr_code) {

            navigateToFragment(null, ApplicationWindows.SHOW_QR_CODE_SCREEN);

        }else if (id == R.id.nav_pay_bills) {

            navigateToFragment(null, ApplicationWindows.PAY_BILLS_SCREEN);

        } else if (id == R.id.nav_logout) {

            logout();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Function for logout the customer
     * and clear the session
     */
    protected void logout(){

        SessionManager sessionManager=new SessionManager(CustomerHomeActivity.this);
        sessionManager.logoutUser();
        finish();
    }

    /**
     * Function to set the ui component of the navigation view
     * @param navView pass navigation view object
     */
    protected void initializeNavigationDrawer(NavigationView navView){

        View hView = navView.inflateHeaderView(R.layout.nav_header_customer_home);
        RoundedImageView imgUserIcon = (RoundedImageView)hView.findViewById(R.id.imgUserIcon);

        TextView lblUsername = (TextView)hView.findViewById(R.id.lblUsername);
        //get default user image
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.user_img);

        imgUserIcon .setImageBitmap(icon);

        String userFName ="";

        String userLName ="";

        try{

            userFName = userInfoResource.getUsrFName();

        }catch (Exception ex){

            userFName ="";
        }

        try{

            userLName = userInfoResource.getUsrLName()==null?"":userInfoResource.getUsrLName();

        }catch (Exception ex){

            userLName="";
        }

        lblUsername.setText(userFName+" "+userLName);

    }

    /**
     * Function to open the navigation view
     * when the app title bar icon clicked
     */
    public void openNavigationDrawer(){

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        drawerLayout.openDrawer(Gravity.LEFT);
    }


    public void setAppTitle(Integer type, String title){

        titleBarTitle.setText(title);

        if(type.equals(0)) {


            titleBarTitle.setVisibility(View.GONE);

            titleBarLogo.setVisibility(View.VISIBLE);



        }else{

            titleBarTitle.setVisibility(View.VISIBLE);

            titleBarLogo.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Function called to move the fragment
     *
     * @param bundle    : The bundle object
     * @param uri       : The URI with the path
     */
    @SuppressLint("CommitTransaction")
    public void navigateToFragment(Bundle bundle, Integer uri) {

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        if (uri == ApplicationWindows.CUSTOMER_HOME_PAGE) {

            /*//show the icons
            imgSearchIcon.setVisibility(View.VISIBLE);
            imgShoppingIcon.setVisibility(View.VISIBLE);*/

            Fragment fragment = new CustomerHomePageTabsFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }


            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.CUSTOMER_HOME_PAGE));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_home);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "DEMO MERCHANT");

        }else if (uri == ApplicationWindows.PAY_WITH_POINTS) {

           /* //hide the icons
            imgSearchIcon.setVisibility(View.GONE);
            imgShoppingIcon.setVisibility(View.GONE);*/

            Fragment fragment = new PayWithPointsFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }

            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.PAY_WITH_POINTS));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentStack.push(ApplicationWindows.PAY_WITH_POINTS);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_pay_with_points);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "PAY WITH POINTS");

        }else if (uri == ApplicationWindows.PROMOTION_DETAILS_VIEW_SCREEN) {

           /* //hide the icons
            imgSearchIcon.setVisibility(View.GONE);
            imgShoppingIcon.setVisibility(View.GONE);*/

            Fragment fragment = new PromotionDetailsViewFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }


            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.PROMOTION_DETAILS_VIEW_SCREEN));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentStack.push(ApplicationWindows.PROMOTION_DETAILS_VIEW_SCREEN);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_home);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "PROMOTION DETAILS VIEW");

        }else if (uri == ApplicationWindows.CUSTOMER_PROFILE_SCREEN) {

           /* //hide the icons
            imgSearchIcon.setVisibility(View.GONE);
            imgShoppingIcon.setVisibility(View.GONE);*/

            Fragment fragment = new CustomerProfileDetailsFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }


            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.CUSTOMER_PROFILE_SCREEN));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentStack.push(ApplicationWindows.CUSTOMER_PROFILE_SCREEN);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_my_profile);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "PROFILE DETAILS");

        }else if (uri == ApplicationWindows.SHOW_QR_CODE_SCREEN) {

           /* //hide the icons
            imgSearchIcon.setVisibility(View.GONE);
            imgShoppingIcon.setVisibility(View.GONE);*/

            Fragment fragment = new ShowCustomerQRCodeFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }


            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.SHOW_QR_CODE_SCREEN));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentStack.push(ApplicationWindows.SHOW_QR_CODE_SCREEN);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_show_qr_code);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "QR CODE");

        }else if (uri == ApplicationWindows.CATALOGUES_SCREEN) {

           /* //hide the icons
            imgSearchIcon.setVisibility(View.GONE);
            imgShoppingIcon.setVisibility(View.GONE);*/

            Fragment fragment = new CataloguesListFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }


            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.CATALOGUES_SCREEN));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentStack.push(ApplicationWindows.CATALOGUES_SCREEN);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_catalogues);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "CATALOGUES");

        }else if (uri == ApplicationWindows.CATALOGUES_DETAILS_VIEW_SCREEN) {

           /* //hide the icons
            imgSearchIcon.setVisibility(View.GONE);
            imgShoppingIcon.setVisibility(View.GONE);*/

            Fragment fragment = new CatalogueDetailsViewFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }


            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.CATALOGUES_DETAILS_VIEW_SCREEN));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentStack.push(ApplicationWindows.CATALOGUES_SCREEN);

            fragmentStack.push(ApplicationWindows.CATALOGUES_DETAILS_VIEW_SCREEN);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_catalogues);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "CATALOGUES");

        }else if (uri == ApplicationWindows.PAY_BILLS_SCREEN) {

            Fragment fragment = new PayMerchantBillsFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }


            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.PAY_BILLS_SCREEN));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentStack.push(ApplicationWindows.PAY_BILLS_SCREEN);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_pay_bills);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "PAY BILLS");

        }else if (uri == ApplicationWindows.CART_ITEMS_VIEW_SCREEN) {

            Fragment fragment = new CatalogueCartItemViewFragment();

            if (bundle != null) {

                fragment.setArguments(bundle);
            }

            fragmentTransaction.replace(R.id.layHomeContent, fragment, String.valueOf(ApplicationWindows.CART_ITEMS_VIEW_SCREEN));

            fragmentStack.clear();

            fragmentStack.push(ApplicationWindows.CUSTOMER_HOME_PAGE);

            fragmentStack.push(ApplicationWindows.CATALOGUES_SCREEN);

            fragmentStack.push(ApplicationWindows.CATALOGUES_DETAILS_VIEW_SCREEN);

            fragmentStack.push(ApplicationWindows.CART_ITEMS_VIEW_SCREEN);

            fragmentTransaction.commit();

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_catalogues);

            // Highlight the selected item, update the title
            menuItem.setChecked(true);

            setAppTitle(1, "CART ITEMS");

        }
    }
}
