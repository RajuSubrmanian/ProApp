package com.inspirenetz.app.proapp.general;

import android.content.Context;
import android.util.Log;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.conn.params.ConnManagerParams;
import cz.msebera.android.httpclient.conn.params.ConnPerRoute;
import cz.msebera.android.httpclient.conn.params.ConnPerRouteBean;
import cz.msebera.android.httpclient.impl.client.BasicCredentialsProvider;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.CoreProtocolPNames;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Raju on 17-Aug-16.
 */
public class APIConnectionManager {

    private static int CONNECTION_TIMEOUT = 60000;


    /**
     * Function to place a get call to the customer profile API that returns a JSONObject
     *
     * @param url 	  - The path to the resource
     * @param params - A list of name value pairs
     * @return 	  - JSONObject
     */
    public JSONObject doCustomerAPIGet(Context context, String url, List<NameValuePair> params) {


        //  Create the GeneralMethods object
        GeneralMethods genMethods = new GeneralMethods();

        // Check if the network connection is available
        boolean isConnected = genMethods.isOnline(context);


        // The JSONObject to hold the info
        JSONObject retData =  null;

        // If the connection is not available
        // then show the toast message
        if ( !isConnected ) {

            try {

                retData = new JSONObject();

                retData.put("status", "failed");

                retData.put("errorcode", "NO_INTERNET_CONNECTION");

            }catch (JSONException ex){

                return null;
            }
        }

        long time0,time1;

        time0 = System.currentTimeMillis();


        // The android httpClient object
        HttpClient httpClient = null;

        try
        {

            // Create the AndroidHttpClient object
            httpClient = new DefaultHttpClient();

            // Create the URL object from the path
            URL urlObj = new URL(url);

            // In this method, we are using only digest based authentication
            // Create host
            HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());

            // Create authscope
            AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());

            // Create a UsernamePasswordCredentials object
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sunil_user", "welcome12");

            // Create and intialize the CredentialsProvider object
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);

            // Create and initialize the HttpContext
            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            // If the params are not null, then we need to
            // append the parameters to the url as a query string
            if ( params != null) {

                if(!url.endsWith("?"))
                    url += "?";

                url += URLEncodedUtils.format(params, "utf-8");
            }


            // Define the HttpGet
            HttpGet job = new HttpGet(url);

            // Set the parameters in the get request
            job.setParams(getHttpParams());

            // Execute the get metho and record the response
            HttpResponse response = httpClient.execute(host,job,credContext);

            // Parse the response to a string.
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            // Set the JSONObject by parsing the result
            retData = new JSONObject(result);

           /* // Close the httpClient
            httpClient.c();
*/


        } catch (SocketTimeoutException e) {

            e.printStackTrace();

            Log.d("socket timeout","socked timeout");

            retData = null;

        } catch (ConnectTimeoutException e) {

            e.printStackTrace();

            Log.d("connection timeout","connection timeout");

            retData = null;

        } catch(Exception e){

            e.printStackTrace();

        }  finally {

            // If the httpClient is not null, then we need to close it.
            if ( httpClient != null) {
                // Close the httpClient
                //httpClient.();
            }

        }

        time1 = System.currentTimeMillis();

        long deltaT = (time1 - time0);
        Log.d("execute time", "Execute took "+deltaT+"ms");

        // Return the retData object
        return retData;

    }


    /**
     * Function to authenticate the user,
     * We pass the username and password and return the json object
     *
     * @param username : user's username / email id
     * @param password : Password for the user
     * @return         : JSONObject on success,
     * 					  Return null if the operation fails
     */
    public JSONObject authenticateUser(String username, String password ) {

        String url = ApplicationConfiguration.CUSTOMER_AUTHENTICATION_URL;

        // The JSONObject to hold the info
        JSONObject retData =  null;

        // The android httpClient object
        HttpClient httpClient = null;

        try
        {
            // Create the AndroidHttpClient object
            httpClient = new DefaultHttpClient();

            // Create the URL object from the path
            URL urlObj = new URL(url);

            // In this method, we are using only digest based authentication
            // Create host
            HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());

            // Create authscope
            AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());

            // Create a UsernamePasswordCredentials object
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

            // Create and intialize the CredentialsProvider object
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);

            // Create and initialize the HttpContext
            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);


            // Define the HttpGet
            HttpGet job = new HttpGet(url);

            // Set the parameters in the get request
            job.setParams(getHttpParams());

            // Execute the get metho and record the response
            HttpResponse response = httpClient.execute(host,job,credContext);

            // Parse the response to a string.
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            result = result.replace("\\\"","'");

            // Set the JSONObject by parsing the result
            retData = new JSONObject(result);

            Log.d("auth data", retData.toString());

            // Close the httpClient
            // httpClient.close();

        } catch (SocketTimeoutException e) {

            e.printStackTrace();

            Log.d("socket timeout", "socked timeout");

            retData = null;

        } catch (ConnectTimeoutException e) {

            e.printStackTrace();

            Log.d("connection timeout", "connection timeout");

            retData = null;

        } catch(Exception e){

            e.printStackTrace();

        } finally {

            // If the httpClient is not null, then we need to close it.
            if ( httpClient != null) {
                // Close the httpClient
                //httpClient.close();
            }

        }

        // Return the retData object
        return retData;
    }


    /**
     * Function to get the HttpParams for the http call
     * Here we set the protolversion, connection timeout and the connections perroute
     *
     * @return - The HttpParams object with specified configurations
     */
    private cz.msebera.android.httpclient.params.HttpParams getHttpParams() {

        //Set the time out information
        cz.msebera.android.httpclient.params.HttpParams httpParams = new BasicHttpParams();

        // Set the protocol version
        httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        // Set connection timeout
        cz.msebera.android.httpclient.params.HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);

        // Set socket timeout
        cz.msebera.android.httpclient.params.HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

        // Set the max connections and max connections per route params
        ConnPerRoute connPerRoute = new ConnPerRouteBean(100);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);


        // Return the httpParams object
        return httpParams;
    }


    /**
     * Function to place a get call to the API that returns a JSONObject
     *
     * @param url 	  - The path to the resource
     * @param params - A list of name value pairs
     * @return 	  - JSONObject
     */
    public JSONObject doAPIGet(Context context, String url, List<NameValuePair> params) {


        //  Create the GeneralMethods object
        GeneralMethods genMethods = new GeneralMethods();

        // Check if the network connection is available
        boolean isConnected = genMethods.isOnline(context);


        // The JSONObject to hold the info
        JSONObject retData =  null;

        // If the connection is not available
        // then show the toast message
        if ( !isConnected ) {

            try {

                retData = new JSONObject();

                retData.put("status", "failed");

                retData.put("errorcode", "NO_INTERNET_CONNECTION");

            }catch (JSONException ex){

                return null;
            }
        }

        long time0,time1;

        time0 = System.currentTimeMillis();

        // Create the SessionManager class
        SessionManager session = new SessionManager(context);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // email
        String username = user.get(SessionManager.KEY_USERNAME);

        // password
        String password = user.get(SessionManager.KEY_PASSWORD);




        // The android httpClient object
        HttpClient httpClient = null;

        try
        {

            // Create the AndroidHttpClient object
            httpClient = new DefaultHttpClient();

            // Create the URL object from the path
            URL urlObj = new URL(url);

            // In this method, we are using only digest based authentication
            // Create host
            HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());

            // Create authscope
            AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());

            // Create a UsernamePasswordCredentials object
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

            // Create and intialize the CredentialsProvider object
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);

            // Create and initialize the HttpContext
            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            // If the params are not null, then we need to
            // append the parameters to the url as a query string
            if ( params != null) {

                if(!url.endsWith("?"))
                    url += "?";

                url += URLEncodedUtils.format(params, "utf-8");
            }


            // Define the HttpGet
            HttpGet job = new HttpGet(url);

            // Set the parameters in the get request
            job.setParams(getHttpParams());

            // Execute the get metho and record the response
            HttpResponse response = httpClient.execute(host,job,credContext);

            // Parse the response to a string.
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            // Set the JSONObject by parsing the result
            retData = new JSONObject(result);

           /* // Close the httpClient
            httpClient.c();
*/


        } catch (SocketTimeoutException e) {

            e.printStackTrace();

            Log.d("socket timeout","socked timeout");

            retData = null;

        } catch (ConnectTimeoutException e) {

            e.printStackTrace();

            Log.d("connection timeout","connection timeout");

            retData = null;

        } catch(Exception e){

            e.printStackTrace();

        }  finally {

            // If the httpClient is not null, then we need to close it.
            if ( httpClient != null) {
                // Close the httpClient
                //httpClient.();
            }

        }

        time1 = System.currentTimeMillis();

        long deltaT = (time1 - time0);
        Log.d("execute time", "Execute took "+deltaT+"ms");

        // Return the retData object
        return retData;

    }


    /**
     * Function to place a get call to the API that returns a JSONObject
     *
     * @param url 	  - The path to the resource
     * @param params - A list of name value pairs
     * @return 	  - JSONObject
     */
    public JSONObject doAPIPost(Context context, String url, List<NameValuePair> params) {

        //  Create the GeneralMethods object
        GeneralMethods genMethods = new GeneralMethods();

        // Check if the network connection is available
        boolean isConnected = genMethods.isOnline(context);

        // The JSONObject to hold the info
        JSONObject retData =  null;


        // If the connection is not available
        // then show the toast message
        if ( !isConnected ) {

            try {

                retData = new JSONObject();

                retData.put("status", "failed");

                retData.put("errorcode", "NO_INTERNET_CONNECTION");

            }catch (JSONException ex){

                return null;
            }
        }

        // Create the SessionManager class
        SessionManager session = new SessionManager(context);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // email
        String username = user.get(SessionManager.KEY_USERNAME);

        // password
        String password = user.get(SessionManager.KEY_PASSWORD);


        // The android httpClient object
        HttpClient httpClient = null;


        try
        {
            // Create the AndroidHttpClient object
            httpClient = new DefaultHttpClient();

            // Create the URL object from the path
            URL urlObj = new URL(url);

            // In this method, we are using only digest based authentication
            // Create host
            HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());

            // Create authscope
            AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());

            // Create a UsernamePasswordCredentials object
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

            // Create and intialize the CredentialsProvider object
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);

            // Create and initialize the HttpContext
            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            // Define the HttpPost
            HttpPost job = new HttpPost(url);

            // Set the params if the params is not null
            if ( params != null ) {

                job.setEntity(new UrlEncodedFormEntity(params));

            }

            // Set the parameters in the get request
            job.setParams(getHttpParams());

            // Execute the get metho and record the response
            HttpResponse response = httpClient.execute(host,job,credContext);

            // Parse the response to a string.
            HttpEntity entity = response.getEntity();

            // Get the resutl as string
            String result = EntityUtils.toString(entity);

            Log.d("post result",result);

            // Set the JSONObject by parsing the result
            retData = new JSONObject(result);

           /* // Close the httpClient
            httpClient.close();*/

        } catch (SocketTimeoutException e) {

            e.printStackTrace();

            Log.d("socket timeout","socked timeout");

            retData = null;

        } catch (ConnectTimeoutException e) {

            e.printStackTrace();

            Log.d("connection timeout","connection timeout");

            retData = null;

        } catch(Exception e){

            e.printStackTrace();

        }  finally {

            // If the httpClient is not null, then we need to close it.
            if ( httpClient != null) {
                /*// Close the httpClient
                httpClient.close();*/
            }

        }


        // Return the retData object
        return retData;

    }


    /**
     * Function to place a get call to the API that returns a JSONObject
     *
     * @param url 	  - The path to the resource
     * @param params - A list of name value pairs
     * @return 	  - JSONObject
     */
    public JSONObject doAPIPostResetLogin(Context context, String url, List<NameValuePair> params) {

        //  Create the GeneralMethods object
        GeneralMethods genMethods = new GeneralMethods();

        // Check if the network connection is available
        boolean isConnected = genMethods.isOnline(context);

        // The JSONObject to hold the info
        JSONObject retData =  null;


        // If the connection is not available
        // then show the toast message
        if ( !isConnected ) {

            try {

                retData = new JSONObject();

                retData.put("status", "failed");

                retData.put("errorcode", "NO_INTERNET_CONNECTION");

            }catch (JSONException ex){

                return null;
            }
        }

        // Create the SessionManager class
        SessionManager session = new SessionManager(context);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // email
        String username = user.get(SessionManager.KEY_USERNAME);

        // password
        String password = user.get(SessionManager.KEY_PASSWORD);


        // The android httpClient object
        HttpClient httpClient = null;


        try
        {
            // Create the AndroidHttpClient object
            httpClient = new DefaultHttpClient();

            // Create the URL object from the path
            URL urlObj = new URL(url);

            // In this method, we are using only digest based authentication
            // Create host
            HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());

            // Create authscope
            AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());

            // Create a UsernamePasswordCredentials object
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials("", "");

            // Create and intialize the CredentialsProvider object
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);

            // Create and initialize the HttpContext
            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            // Define the HttpPost
            HttpPost job = new HttpPost(url);

            // Set the params if the params is not null
            if ( params != null ) {

                job.setEntity(new UrlEncodedFormEntity(params));

            }

            // Set the parameters in the get request
            job.setParams(getHttpParams());

            // Execute the get metho and record the response
            HttpResponse response = httpClient.execute(host,job,credContext);

            // Parse the response to a string.
            HttpEntity entity = response.getEntity();

            // Get the resutl as string
            String result = EntityUtils.toString(entity);

            Log.d("post result",result);

            // Set the JSONObject by parsing the result
            retData = new JSONObject(result);

           /* // Close the httpClient
            httpClient.close();*/

        } catch (SocketTimeoutException e) {

            e.printStackTrace();

            Log.d("socket timeout","socked timeout");

            retData = null;

        } catch (ConnectTimeoutException e) {

            e.printStackTrace();

            Log.d("connection timeout","connection timeout");

            retData = null;

        } catch(Exception e){

            e.printStackTrace();

        }  finally {

            // If the httpClient is not null, then we need to close it.
            if ( httpClient != null) {
                /*// Close the httpClient
                httpClient.close();*/
            }

        }


        // Return the retData object
        return retData;

    }



    /**
     * Function to place a get call to the API that returns a JSONObject
     *
     * @param url 	  - The path to the resource
     * @param params - A list of name value pairs
     * @return 	  - JSONObject
     */
    public JSONObject doAPIPayOtpGet(Context context, String url, List<NameValuePair> params) {


        //  Create the GeneralMethods object
        GeneralMethods genMethods = new GeneralMethods();

        // Check if the network connection is available
        boolean isConnected = genMethods.isOnline(context);


        // The JSONObject to hold the info
        JSONObject retData =  null;

        // If the connection is not available
        // then show the toast message
        if ( !isConnected ) {

            try {

                retData = new JSONObject();

                retData.put("status", "failed");

                retData.put("errorcode", "NO_INTERNET_CONNECTION");

            }catch (JSONException ex){

                return null;
            }
        }

        long time0,time1;

        time0 = System.currentTimeMillis();

        // Create the SessionManager class
        SessionManager session = new SessionManager(context);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // email
        String username = user.get(SessionManager.KEY_USERNAME);

        // password
        String password = user.get(SessionManager.KEY_PASSWORD);




        // The android httpClient object
        HttpClient httpClient = null;

        try
        {

            // Create the AndroidHttpClient object
            httpClient = new DefaultHttpClient();

            // Create the URL object from the path
            URL urlObj = new URL(url);

            // In this method, we are using only digest based authentication
            // Create host
            HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());

            // Create authscope
            AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());

            // Create a UsernamePasswordCredentials object
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials("8190815673", "welcome12");

            // Create and intialize the CredentialsProvider object
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);

            // Create and initialize the HttpContext
            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            // If the params are not null, then we need to
            // append the parameters to the url as a query string
            if ( params != null) {

                if(!url.endsWith("?"))
                    url += "?";

                url += URLEncodedUtils.format(params, "utf-8");
            }


            // Define the HttpGet
            HttpGet job = new HttpGet(url);

            // Set the parameters in the get request
            job.setParams(getHttpParams());

            // Execute the get metho and record the response
            HttpResponse response = httpClient.execute(host,job,credContext);

            // Parse the response to a string.
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            // Set the JSONObject by parsing the result
            retData = new JSONObject(result);

           /* // Close the httpClient
            httpClient.c();
*/


        } catch (SocketTimeoutException e) {

            e.printStackTrace();

            Log.d("socket timeout","socked timeout");

            retData = null;

        } catch (ConnectTimeoutException e) {

            e.printStackTrace();

            Log.d("connection timeout","connection timeout");

            retData = null;

        } catch(Exception e){

            e.printStackTrace();

        }  finally {

            // If the httpClient is not null, then we need to close it.
            if ( httpClient != null) {
                // Close the httpClient
                //httpClient.();
            }

        }

        time1 = System.currentTimeMillis();

        long deltaT = (time1 - time0);
        Log.d("execute time", "Execute took "+deltaT+"ms");

        // Return the retData object
        return retData;

    }

}


