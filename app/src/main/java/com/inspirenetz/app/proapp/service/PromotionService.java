package com.inspirenetz.app.proapp.service;

import android.content.Context;

import com.google.gson.Gson;
import com.inspirenetz.app.proapp.dictionary.APIResponseStatus;
import com.inspirenetz.app.proapp.general.APIConnectionManager;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.resources.MembershipResponse;
import com.inspirenetz.app.proapp.resources.PromotionListResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;

/**
 * Created by raju on 2/4/17.
 */

public class PromotionService {

    /**
     * Method to get the promotion list with the api
     * @param context context of the application
     * @return return api response
     */
    public PromotionListResponse getPromotions(Context context) {

        // The url to fetch the promotion information
        String url = ApplicationConfiguration.MERCHANT_PROMOTION_LIST;

        // The JSONObject to hold the info
        PromotionListResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIGet(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new PromotionListResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),PromotionListResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new PromotionListResponse();
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
