package com.inspirenetz.app.proapp.service;

import android.content.Context;

import com.google.gson.Gson;
import com.inspirenetz.app.proapp.dictionary.APIResponseStatus;
import com.inspirenetz.app.proapp.general.APIConnectionManager;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.resources.OneTimePasswordResponse;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Raju on 21-Aug-16.
 */
public class OTPService {

    /**
     * Function to generate otp for customer registration
     * and send to customer mobile
     * @param context application context
     * @param mobileNo customer mobile no
     * @return response
     */
    public OneTimePasswordResponse generateOTP(Context context, String mobileNo){

        String url = ApplicationConfiguration.GENERATE_CUSTOMER_REGISTER_OTP_API_URL;

        // The JSONObject to hold the info
        OneTimePasswordResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Set the mobile
        params.add(new BasicNameValuePair("userLoginId", mobileNo));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPostResetLogin(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new OneTimePasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            String data = json_obj.getString("data");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),OneTimePasswordResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new OneTimePasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;

            }else if (data.equals("")){

                retData = new OneTimePasswordResponse();
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
     * Function to validate the otp
     * for customer registration
     * @param context application context
     * @param mobileNo customer mobile no
     * @return response
     */
    public OneTimePasswordResponse validateRegisterOTP(Context context, String mobileNo, String otpCode){

        String url = ApplicationConfiguration.VALIDATE_CUSTOMER_REGISTER_OTP_API_URL;

        // The JSONObject to hold the info
        OneTimePasswordResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Set the mobile
        params.add(new BasicNameValuePair("userLoginId", mobileNo));

        // Set the otp code
        params.add(new BasicNameValuePair("otpCode", otpCode));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPostResetLogin(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new OneTimePasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");
            String data = json_obj.getString("data");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),OneTimePasswordResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new OneTimePasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;

            }else if (data.equals("")){

                retData = new OneTimePasswordResponse();
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
     * Function to generate the otp for pay function
     * and send to customer mobile no
     * @param context application context
     * @param mobileNo customer loyalty id
     * @param merchantNo selected merchant no
     * @param otpType otp type fo pay function
     * @return response
     */
    public OneTimePasswordResponse generatePayOTP(Context context, String mobileNo, String merchantNo, String otpType){

        String url = ApplicationConfiguration.GENERATE_PAY_OTP_API_URL;

        // The JSONObject to hold the info
        OneTimePasswordResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Set the mobile
        params.add(new BasicNameValuePair("mobile", mobileNo));

        // Set the mobile
        params.add(new BasicNameValuePair("merchantNo", merchantNo));

        // Set the mobile
        params.add(new BasicNameValuePair("otpType", otpType));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPayOtpGet(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new OneTimePasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            String data = json_obj.getString("data");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),OneTimePasswordResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new OneTimePasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode(errorCode);

                return retData;

            }else if (data.equals("")){

                retData = new OneTimePasswordResponse();
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


    public OneTimePasswordResponse generateResetOTP(Context context, String mobileNo){

        String url = ApplicationConfiguration.GENERATE_RESET_PASSWORD_OTP_API_URL;

        // The JSONObject to hold the info
        OneTimePasswordResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Set the mobile
        params.add(new BasicNameValuePair("userLoginId", mobileNo));

        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPostResetLogin(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new OneTimePasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String status=json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),OneTimePasswordResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new OneTimePasswordResponse();
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


