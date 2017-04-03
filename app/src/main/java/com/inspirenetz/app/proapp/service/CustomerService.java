package com.inspirenetz.app.proapp.service;

import android.content.Context;

import com.google.gson.Gson;
import com.inspirenetz.app.proapp.dictionary.APIResponseStatus;
import com.inspirenetz.app.proapp.general.APIConnectionManager;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.resources.CustomerActivityListResponse;
import com.inspirenetz.app.proapp.resources.CustomerProfileInfoResource;
import com.inspirenetz.app.proapp.resources.CustomerProfileInfoResponse;
import com.inspirenetz.app.proapp.resources.CustomerProfileInfoUpdateResponse;
import com.inspirenetz.app.proapp.resources.CustomerSaveResponse;
import com.inspirenetz.app.proapp.resources.CustomerSearchResponse;
import com.inspirenetz.app.proapp.resources.UserAuthenticationResponse;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Raju on 17-Aug-16.
 */
public class CustomerService {

    public CustomerSearchResponse getCustomerInfo(Context context , String mobile) {


        // The url to fetch the promotion information
        String url = ApplicationConfiguration.CUSTOMER_PROFILE_URL;

        // The JSONObject to hold the info
        CustomerSearchResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Set the cardnumber param
        params.add(new BasicNameValuePair("customer_search_text", mobile));


        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doCustomerAPIGet(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new CustomerSearchResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),CustomerSearchResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new CustomerSearchResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;
            }

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return the retData object
        return retData;

    }

    /**
     * Function to register customer
     *
     * @param context       -   The applicationcontext from the activity
     * @param //customerResource
     *
     * @return -  JSONObject with data in case of succes
     * 			   Null in the case of failure
     */
    public CustomerSaveResponse postRegisterCustomer(Context context, String mobile, String fname, String email, String password) {

        // The url to fetch the promotion information
        String url = ApplicationConfiguration.CUSTOMER_REGISTRATION_API_URL;

        // The JSONObject to hold the info
        CustomerSaveResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Set the firstname
        params.add(new BasicNameValuePair("firstName", fname));

        // Set the customer mobile
        params.add(new BasicNameValuePair("mobile", mobile));

        // set the customer email
        params.add(new BasicNameValuePair("email", email));

        // Set the customer address
        params.add(new BasicNameValuePair("password", password));


        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPostResetLogin(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new CustomerSaveResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),CustomerSaveResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new CustomerSaveResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;
            }

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return the retData object
        return retData;
    }


    /**
     * Funtion to authenticate the login customer details
     * @param mobile customer mobile number
     * @param password customer password
     * @return response object
     */
    public UserAuthenticationResponse authenticateUser(String mobile, String password) {


        // The JSONObject to hold the info
        UserAuthenticationResponse retData =  null;

        // store mobile no
        String mobileNo = mobile;

        // store password
        String cusPassword = password;

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().authenticateUser(mobileNo, cusPassword);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new UserAuthenticationResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),UserAuthenticationResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new UserAuthenticationResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;
            }

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return the retData object
        return retData;
    }

    /**
     * Methot to get the customer profile details with merchant no
     * @param context application context
     * @param merchantNo pass merchant no
     * @return return api response
     */
    public CustomerProfileInfoResponse getCustomerInfoWithMerchantNo(Context context, String merchantNo) {


        // The url to fetch the promotion information
        String url = ApplicationConfiguration.FETCH_CUSTOMER_PROFILE_DETAILS_API_URL;

        url += "/" + merchantNo;

        // The JSONObject to hold the info
        CustomerProfileInfoResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();


        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIGet(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new CustomerProfileInfoResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),CustomerProfileInfoResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new CustomerProfileInfoResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;
            }

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return the retData object
        return retData;

    }

    /**
     * Method to post the customer information
     * @param context application context
     * @param customerProfileInfoResource object of CustomerProfileInfoResource
     * @return api response
     */
    public CustomerProfileInfoUpdateResponse postCustomerProfileInfoUpdate(Context context, CustomerProfileInfoResource customerProfileInfoResource) {


        // The url to fetch the promotion information
        String url = ApplicationConfiguration.UPDATE_CUSTOMER_PROFILE_DETAILS_API_URL;

        // The JSONObject to hold the info
        CustomerProfileInfoUpdateResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //get first name
        params.add(new BasicNameValuePair("cusFName", customerProfileInfoResource.getCusFName()));

        //get last name
        params.add(new BasicNameValuePair("cusLName", customerProfileInfoResource.getCusLName()));

        //get email
        params.add(new BasicNameValuePair("cusEmail", customerProfileInfoResource.getCusEmail()));

        //get gender
        params.add(new BasicNameValuePair("cspGender", customerProfileInfoResource.getCspGender()));

        //get family status
        params.add(new BasicNameValuePair("cspFamilyStatus", customerProfileInfoResource.getCspFamilyStatus()));

        if(customerProfileInfoResource.getCspCustomerBirthday()!=null && !customerProfileInfoResource.getCspCustomerBirthday().equals("")) {

            //get birth date
            params.add(new BasicNameValuePair("cspCustomerBirthday", customerProfileInfoResource.getCspCustomerBirthday()));
        }

        if(customerProfileInfoResource.getCspCustomerAnniversary()!=null && !customerProfileInfoResource.getCspCustomerAnniversary().equals("")) {

            //get anniversary
            params.add(new BasicNameValuePair("cspCustomerAnniversary", customerProfileInfoResource.getCspCustomerAnniversary()));
        }

        //get merchant no
        params.add(new BasicNameValuePair("cusMerchantNo", customerProfileInfoResource.getCusMerchantNo()));

        //get mobile no
        params.add(new BasicNameValuePair("cusMobile", customerProfileInfoResource.getCusMobile()));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPost(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new CustomerProfileInfoUpdateResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String data = json_obj.getString("data");

            if(data.equals("true")){

                retData = new CustomerProfileInfoUpdateResponse();
                retData.setStatus(APIResponseStatus.SUCCESS);

                return retData;
            }

            String status = json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),CustomerProfileInfoUpdateResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new CustomerProfileInfoUpdateResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;
            }

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return the retData object
        return retData;

    }

    /**
     * Method to call api for get the customer activities
     * @param context application context
     * @param merchantNo merchant number
     * @param fromDate pass from date of activity
     * @param endDate pass end date of activity
     * @return api response
     */
    public CustomerActivityListResponse getCustomerActivityList(Context context, String merchantNo, String fromDate, String endDate) {

        // The url to fetch the promotion information
        String url = ApplicationConfiguration.FETCH_CUSTOMER_ACTIVITY_LIST;

        // The JSONObject to hold the info
        CustomerActivityListResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //get merchant no
        params.add(new BasicNameValuePair("merchantNo", merchantNo));

       /* //get from date
        params.add(new BasicNameValuePair("fromDate", fromDate));

        //get end date
        params.add(new BasicNameValuePair("endDate", endDate));*/

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIGet(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new CustomerActivityListResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String data = json_obj.getString("data");

            /*if(data.equals("true")){

                retData = new CustomerActivityListResponse();
                retData.setStatus(APIResponseStatus.SUCCESS);

                return retData;
            }*/

            String status = json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),CustomerActivityListResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new CustomerActivityListResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;
            }

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return the retData object
        return retData;

    }
}
