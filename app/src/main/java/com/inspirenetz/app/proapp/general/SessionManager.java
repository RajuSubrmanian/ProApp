package com.inspirenetz.app.proapp.general;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inspirenetz.app.proapp.resources.CatalogueCartItemResource;
import com.inspirenetz.app.proapp.resources.CatalogueResource;
import com.inspirenetz.app.proapp.resources.MembershipResource;
import com.inspirenetz.app.proapp.resources.UserInfoResource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Raju on 17-Aug-16.
 */
public class SessionManager {

    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file password
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User password (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_USER_INFO = "userInfo";

    // Email address (make variable public to access from outside)
    public static final String KEY_USERNAME = "username";

    public static final String KEY_CUSTOMER_CART_COLLECTION = "customerCartCollection";

    public static final String KEY_MEMBERSHIP_INFO = "membershipInfo";

    public static final String IS_CAMERA_PERMISSION_ENABLED = "isCameraPermissionEnabled";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String username, String password, UserInfoResource userInfoResource){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_USERNAME, username);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);

        GeneralMethods generalMethods=new GeneralMethods();

        Gson gson = generalMethods.getGsonObject();

        String userInfoJsonObject = gson.toJson(userInfoResource);

        editor.putString(KEY_USER_INFO, userInfoJsonObject);

        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user password
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        String json = pref.getString(KEY_USER_INFO, null);

        user.put(KEY_USER_INFO,json);

        // return user
        return user;
    }

    public UserInfoResource getUserInfo(){

        GeneralMethods generalMethods=new GeneralMethods();

        Gson gson=generalMethods.getGsonObject();

        String json = pref.getString(KEY_USER_INFO, null);

        UserInfoResource userInfoResource=gson.fromJson(json,UserInfoResource.class);

        return userInfoResource;
    }

    /**
     * Method to create the session for catalogue
     * @param customerCartHashMap pass param list object
     */
    public void createOrUpdateCustomerCartCollection(HashMap<String, List<CatalogueCartItemResource>> customerCartHashMap){

        GeneralMethods generalMethods=new GeneralMethods();

        Gson gson = generalMethods.getGsonObject();

        String catalogueInfoJsonObject = gson.toJson(customerCartHashMap);

        editor.putString(KEY_CUSTOMER_CART_COLLECTION, catalogueInfoJsonObject);

        // commit changes
        editor.commit();
    }

    /**
     * Method to get the stored values in session
     * @return stored values
     */
    public HashMap<String, List<CatalogueCartItemResource>> getCustomerCartCollection(){

        GeneralMethods generalMethods=new GeneralMethods();

        Gson gson=generalMethods.getGsonObject();

        String json = pref.getString(KEY_CUSTOMER_CART_COLLECTION, null);

        Type collectionType = new TypeToken<HashMap<String, List<CatalogueCartItemResource>>>(){}.getType();

        HashMap<String,List<CatalogueCartItemResource>> catalogueCartItemsResources = (HashMap<String,List<CatalogueCartItemResource>>) gson.fromJson( json , collectionType);

        return catalogueCartItemsResources;
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){

        // Check login status
        if(!this.isLoggedIn()){
           /* // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/
        }

    }


    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        //clear the particular details of customer
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_USER_INFO);
        editor.remove(KEY_MEMBERSHIP_INFO);

        //editor.clear();
        editor.commit();
    }


    /**
     * Method to add the customer catalogue items to the cart
     * if product (productNo) is already added the cart need to increase the quantity
     * or else need to update the items to the cart
     */
    public void addCustomerCatalogueItemsToCart(String username, CatalogueResource catalogueResource, String buttonAction, String processResponse){

        //check if exist
        boolean isExist = false;

        //get stored customer cart collection in hash map
        HashMap<String, List<CatalogueCartItemResource>> catalogueListResources = getCustomerCartCollection();

        //check if customer cart collection is null in hasp map
        if(catalogueListResources==null){

            //assign to hash map
            catalogueListResources = new HashMap<String, List<CatalogueCartItemResource>>();
        }

        //get cart collection by username(user login id) in list
        List<CatalogueCartItemResource> catalogueCartItemResources = catalogueListResources.get(username);


        //check if cart collection by username(user login id) is null in list
        if(catalogueCartItemResources ==null){

            //assign to array list
            catalogueCartItemResources = new ArrayList<CatalogueCartItemResource>();
        }

        //if the cart collection by username(user login id) is not null
        //iterate the list
        for (CatalogueCartItemResource catalogueCartItemResource : catalogueCartItemResources){

            //check if product is already exist in the cart
            //if already exist increase the cart quantity
            if(catalogueResource.getCatProductNo().equals(String.valueOf(catalogueCartItemResource.getCatProductNo()))){

                int itemQuantity= catalogueCartItemResource.getQty();

                if(buttonAction.equals("decreaseQuantity") && (!buttonAction.equals(""))){

                    //decrease the quantity
                    itemQuantity-=1;

                    catalogueCartItemResource.setQty(itemQuantity);

                    isExist = true;

                    break;

                }else if(buttonAction.equals("removeItem") && (!buttonAction.equals(""))){

                    //clear the data to remove the specified item in the cart
                    catalogueCartItemResources.remove(catalogueCartItemResource);

                    isExist = true;

                    break;

                }else if (buttonAction.equals("redemptionProcess") && (!buttonAction.equals(""))){

                    //change the status
                    catalogueCartItemResource.setStatus(processResponse);

                    isExist = true;

                    break;

                }else {

                    //increase the quantity
                    itemQuantity+=1;

                    catalogueCartItemResource.setQty(itemQuantity);

                    isExist = true;

                    break;

                }
            }
        }

        //if the product is not exist add the items to the cart
        if(!isExist){

            CatalogueCartItemResource catalogueCartItemResource = new CatalogueCartItemResource();

            catalogueCartItemResource.setCatProductNo(catalogueResource.getCatProductNo());
            catalogueCartItemResource.setCatalogueResource(catalogueResource);
            catalogueCartItemResource.setQty(1);
            catalogueCartItemResource.setStatus("available");

            catalogueCartItemResources.add(catalogueCartItemResource);
        }

        //put the values to hash map (customer, items)
        catalogueListResources.put(username, catalogueCartItemResources);

        //call the method and pass the hash map values to store in shared preference
        createOrUpdateCustomerCartCollection(catalogueListResources);

    }

    public void storeCameraPermissionStatus(String camera, boolean b){

        editor.putBoolean(camera, b);
        editor.commit();
    }


    public boolean isCameraPermissionEnabled() {

        return pref.getBoolean(IS_CAMERA_PERMISSION_ENABLED, false);
    }
}
