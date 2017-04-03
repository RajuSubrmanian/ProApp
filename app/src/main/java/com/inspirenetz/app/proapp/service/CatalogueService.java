package com.inspirenetz.app.proapp.service;

import android.content.Context;

import com.google.gson.Gson;
import com.inspirenetz.app.proapp.dictionary.APIResponseStatus;
import com.inspirenetz.app.proapp.general.APIConnectionManager;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.CatalogueRedemptionResponse;
import com.inspirenetz.app.proapp.resources.CatalogueResource;
import com.inspirenetz.app.proapp.resources.CatalogueResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by raju on 2/16/17.
 */

public class CatalogueService {

    /**
     * Method to get the catalogue items
     * @param context application context
     * @param filter filter by name
     * @param query search the catalogue using query
     * @param catMerchantNo certain catalogue merchant no
     * @param catCategory specified category
     * @param sortOption sorting option
     * @param channel default channel
     * @return return api response
     */
    public CatalogueResponse getCatalogueList(Context context, String filter, String query, String catMerchantNo, String catCategory, String sortOption, String channel) {

        // The url to fetch the catalogue information
        String url = ApplicationConfiguration.FETCH_CATALOGUE_LIST_API_URL;

        // The JSONObject to hold the info
        CatalogueResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //get filter
        params.add(new BasicNameValuePair("filter", filter));

        //get query
        params.add(new BasicNameValuePair("query", query));

        //get merchant no
        params.add(new BasicNameValuePair("catMerchantNo", catMerchantNo));

        //get category
        params.add(new BasicNameValuePair("catCategory", catCategory));

        //get sort option
        params.add(new BasicNameValuePair("sortOption", sortOption));

        //get channel
        params.add(new BasicNameValuePair("channel", channel));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIGet(context,url, params);

        try {

            // If the json_obj status is "success", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new CatalogueResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status = json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),CatalogueResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new CatalogueResponse();
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
     * Method to post the catalogue redemption details to server
     * which is available in the cart
     * @param context application context
     * @param prdcode product code
     * @param quantity item quantity
     * @param channel redemption channel
     * @param destLoyaltyId loyalty id
     * @param merchantno merchant no
     * @return api response
     */
    public CatalogueRedemptionResponse postCatalogueRedemption(Context context, String prdcode, int quantity, String channel, String destLoyaltyId, String merchantno, String cusMobileNo, CatalogueResource catalogueResource) {

        // The url to post the redemption information
        String url = ApplicationConfiguration.CATALOGUE_REDEMPTION_API_URL;

        // The JSONObject to hold the info
        CatalogueRedemptionResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //get product code
        params.add(new BasicNameValuePair("prdcode", prdcode));

        //get item quantity
        params.add(new BasicNameValuePair("quantity", String.valueOf(quantity)));

        //get redemption channel
        params.add(new BasicNameValuePair("channel", channel));

        //get loyalty id
        params.add(new BasicNameValuePair("destLoyaltyId", destLoyaltyId));

        //get merchant no
        params.add(new BasicNameValuePair("merchantno", merchantno));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPost(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new CatalogueRedemptionResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            SessionManager sessionManager = new SessionManager(context);

            String status = json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                sessionManager.addCustomerCatalogueItemsToCart(cusMobileNo, catalogueResource,"redemptionProcess","success");

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),CatalogueRedemptionResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                GeneralMethods generalMethods=new GeneralMethods();
                String errorMessage = generalMethods.getTextualErrorString(context, errorCode);

                sessionManager.addCustomerCatalogueItemsToCart(cusMobileNo, catalogueResource,"redemptionProcess",errorMessage);

                retData = new CatalogueRedemptionResponse();
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
