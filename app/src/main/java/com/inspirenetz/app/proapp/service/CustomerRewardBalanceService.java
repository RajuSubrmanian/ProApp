package com.inspirenetz.app.proapp.service;

import android.content.Context;

import com.google.gson.Gson;
import com.inspirenetz.app.proapp.dictionary.APIResponseStatus;
import com.inspirenetz.app.proapp.general.APIConnectionManager;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.resources.RewardBalanceResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Raju on 02-Sep-16.
 */
public class CustomerRewardBalanceService {


    /** Function to get customer reward balance
     * @param context application context
     * @param merchantNO based on selected merchant no
     * @return json response
     */
    public RewardBalanceResponse getRewardBalance(Context context , String merchantNO) {

        // The url to fetch the promotion information
        String url = ApplicationConfiguration.CUSTOMER_REWARD_BALANCE_API_URL;

        // The JSONObject to hold the info
        RewardBalanceResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Set the loyatly id
        params.add(new BasicNameValuePair("merchant_no", merchantNO));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIGet(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {

                retData = new RewardBalanceResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            String data = json_obj.getString("data");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),RewardBalanceResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new RewardBalanceResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;

            }else if (data.equals("")){

                retData = new RewardBalanceResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO DATA");

            }

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return the retData object
        return retData;

    }

}
