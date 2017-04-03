package com.inspirenetz.app.proapp.service;

import android.content.Context;

import com.google.gson.Gson;
import com.inspirenetz.app.proapp.dictionary.APIResponseStatus;
import com.inspirenetz.app.proapp.general.APIConnectionManager;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.resources.PayMerchantBillsResponse;
import com.inspirenetz.app.proapp.resources.PayWithPointsResource;
import com.inspirenetz.app.proapp.resources.PayWithPointsResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Raju on 01-Sep-16.
 */
public class PayService {

    /**
     * Method to post the pay with points details
     * @param context application context
     * @param payWithPointsResource pass all parameters with this object
     * @return return response
     */
    public PayWithPointsResponse postPayWithPoints(Context context, PayWithPointsResource payWithPointsResource) {

        // The url to fetch the promotion information
        String url = ApplicationConfiguration.PAY_WITH_POINTS_API_URL;

        // The JSONObject to hold the info
        PayWithPointsResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // add merchant no
        params.add(new BasicNameValuePair("merchantNo",payWithPointsResource.getMerchantNo()));

        // add merchantIdentifier
        params.add(new BasicNameValuePair("merchantIdentifier",payWithPointsResource.getMerchantIdentifier()));

        // add amount
        params.add(new BasicNameValuePair("amount",payWithPointsResource.getAmount()));

        // add pin
        params.add(new BasicNameValuePair("pin",payWithPointsResource.getPin()));

        // add channel
        params.add(new BasicNameValuePair("channel",payWithPointsResource.getChannel()));

        // add otp code
        params.add(new BasicNameValuePair("otpCode",payWithPointsResource.getOtpCode()));

        // add ref
        params.add(new BasicNameValuePair("ref",payWithPointsResource.getRef()));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPost(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new PayWithPointsResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            String data = json_obj.getString("data");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),PayWithPointsResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new PayWithPointsResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;

            }else if (data.equals("")){

                retData = new PayWithPointsResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_DATA");

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
     * Method to call the api of pay bill
     * @param context application context
     * @param merchantNo merchant no
     * @param merchantIdentifier merchant, who wants payment
     * @param ref ref of the payment
     * @param amount amount of points
     * @param pin pin for the payment
     * @return return the api/server response
     */
    public PayMerchantBillsResponse postPayMerchantBill(Context context, String merchantNo, String merchantIdentifier, String ref, String amount, String pin, String channel, String otpCode) {

        // The url to fetch the promotion information
        String url = ApplicationConfiguration.PAY_UTILITY_BILL_API_URL;

        // The JSONObject to hold the info
        PayMerchantBillsResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // add merchant no
        params.add(new BasicNameValuePair("merchantNo",merchantNo));

        // add merchantIdentifier
        params.add(new BasicNameValuePair("merchantIdentifier",merchantIdentifier));

        // add amount
        params.add(new BasicNameValuePair("amount",amount));

        // add pin
        params.add(new BasicNameValuePair("pin",pin));

        // add channel
        params.add(new BasicNameValuePair("channel",channel));

        // add otp code
        params.add(new BasicNameValuePair("otpCode",""));

        // add ref
        params.add(new BasicNameValuePair("ref",ref));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPost(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new PayMerchantBillsResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            String data = json_obj.getString("data");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),PayMerchantBillsResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new PayMerchantBillsResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;

            }else if (data.equals("")){

                retData = new PayMerchantBillsResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_DATA");

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
