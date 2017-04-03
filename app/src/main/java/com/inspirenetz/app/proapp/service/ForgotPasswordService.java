package com.inspirenetz.app.proapp.service;

import android.content.Context;

import com.google.gson.Gson;
import com.inspirenetz.app.proapp.dictionary.APIResponseStatus;
import com.inspirenetz.app.proapp.general.APIConnectionManager;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.resources.ForgotPasswordResponse;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Mishab on 23-08-2016.
 */
public class ForgotPasswordService {

    public ForgotPasswordResponse resetPassword (Context context, String mobileNo, String newPassword, String otpCode){


        String url = ApplicationConfiguration.VALIDATE_RESET_PASSWORD_API_URL;

        // The JSONObject to hold the info
        ForgotPasswordResponse retData =  null;

        // Set up the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Set the mobile
        params.add(new BasicNameValuePair("userLoginId", mobileNo));

        params.add(new BasicNameValuePair("password", newPassword));

        params.add(new BasicNameValuePair("otpcode", otpCode));


        // Place a API Get call using the API Connection Manager
        JSONObject json_obj = new APIConnectionManager().doAPIPostResetLogin(context,url, params);

        try {

            // If the json_obj status is "succes", we set the retData to be
            // the json array
            // else set the retData to null
            if (json_obj == null ) {


                retData = new ForgotPasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("NO_RESPONSE_FROM_SERVER");

                return retData;

            }

            String data = json_obj.getString("data");

            if(data.equals("ERR_INVALID_OTP")){

                retData = new ForgotPasswordResponse();
                retData.setStatus(APIResponseStatus.FAILED);
                retData.setErrorcode("ERR_INVALID_OTP");

                return retData;

            }

            String status=json_obj.getString("status");

            if(status.equals(APIResponseStatus.SUCCESS)) {

                GeneralMethods generalMethods=new GeneralMethods();

                Gson gson=generalMethods.getGsonObject();

                retData=gson.fromJson(json_obj.toString(),ForgotPasswordResponse.class);

                return retData;

            }else if(status.equals(APIResponseStatus.FAILED)){

                String errorCode=json_obj.getString("errorcode");

                retData = new ForgotPasswordResponse();
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
