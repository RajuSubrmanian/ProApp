package com.inspirenetz.app.proapp.general;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inspirenetz.app.proapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by raju on 2/1/17.
 */

public class GeneralMethods {

    /**
     * Function to check if the network connection is available or not
     * Here we need to pass the context of the activity that will be used
     * to get the SystemService
     * NOTE: For checking this, we need to have the
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * permission in the AndroidManifest.xml
     *
     * @param context    : The ActivtyContext from which we are calling the service
     * @return  		  : Return true if the connectivity is avialble
     * 						Return false if no connectivity is not available
     */
    public boolean isOnline(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;

        }

        return false;
    }

    /**
     * Function to show the Toast message on the success
     * We need to pass the context and the passed message
     *
     * @param context - The Activity context
     * @param message - The message to be displayed
     */
    public void showToastMessage(Context context,String message ) {

        // The duration to show the Toast message
        int duration = Toast.LENGTH_LONG;

        // Create the toast object
        Toast toast = Toast.makeText(context, message, duration);

        // Show the toast message
        toast.show();

    }

    public void showErrorPopupMessage(Context context){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle("Error");

        // Setting Dialog Message
        alertDialog.setMessage("Sorry you can't use this QR Code");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_status_failed);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                return;
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    /**
     * Function to show the progress dialog while calling the api
     * @param context - the activity context
     * @param heading - heading of the dialog
     * @param text - text message of the dialog
     * @return dialog
     */
    public Dialog getCustomPogressDialog(Context context, String heading, String text) {

        // Declare the customer dialog
        Dialog dlgProgress = new Dialog(context);

        // 	Set no title for the dialog
        dlgProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set the content view to the customer_alert layout
        dlgProgress.setContentView(R.layout.layout_custom_process_progress);

        // Cancel the dialog when touched outside.
        dlgProgress.setCanceledOnTouchOutside(false);

        // Set the main heading
        TextView dlgHeading = (TextView) dlgProgress.findViewById(R.id.layCustomProgressHeading);
        dlgHeading.setText(heading);

        // set the info
        TextView dlgInfo = (TextView) dlgProgress.findViewById(R.id.layCustomProgressInfo);
        dlgInfo.setText(text);

        // Return the refenrece to the dialog
        return dlgProgress;

    }

    /**
     * Method to change the date format
     * @param date pass date
     * @return return new date format
     */
    public String getDateFormat(Context context, String date){

        String newDateFormat = "";

        try{

            final String OLD_FORMAT = "yyyy-MM-dd";
            final String NEW_FORMAT = "dd MMM yyyy";

            String oldDateString = date;

            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateFormat = sdf.format(d);

        }catch (ParseException e){

            e.printStackTrace();
        }

        return newDateFormat;
    }

    /***
     * Method for getting app date formatter
     * @return
     */
    public SimpleDateFormat getAppDateFormat(){

        SimpleDateFormat dateFormat= new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        return dateFormat;
    }

    /***
     * Method for getting server date formatter
     * @return
     */
    public SimpleDateFormat getServerDateFormat(){

        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        return dateFormat;
    }

    /**
     * Function to return the standard GSon object
     *
     * @return : The Gson object
     */
    public Gson getGsonObject() {

        // create the gson instance
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

    }


    /**
     * Function to get the textual string for the given error message
     * from the server
     *
     * @param errorCode - The message from the server
     * @return - The string represntation of the message
     */
    public String getTextualErrorString(Context context,String errorCode) {

        String strError = "";

        int errorResource;

        switch (errorCode) {
            case "ERR_INVALID_INPUT":

                errorResource = R.string.error_invalid_input;

                break;
            case "ERR_NO_ID_FIELD":

                strError = "Please specify loyaltyid/email/mobile";
                errorResource = R.string.error_no_id_field;

                break;
            case "ERR_DUPLICATE_ID_FIELD":

                strError = "Loyalty Id/Email/Mobile already exists";
                errorResource = R.string.error_duplicate_id_field;

                break;
            case "ERR_OPERATION_FAILED":

                errorResource = R.string.error_operation_failed;

                break;
            case "ERR_NOT_FOUND":

                errorResource = R.string.error_not_found;

                break;
            case "ERR_NO_DATA":

                errorResource = R.string.error_no_data;

                break;
            case "ERR_NO_DATA_FOUND":

                errorResource = R.string.error_no_data_found;
                break;
            case "ERR_NO_BALANCE":

                errorResource = R.string.error_no_balance;

                break;
            case "ERR_INSUFFICIENT_POINT_BALANCE":

                errorResource = R.string.err_insufficient_point_balance;

                break;
            case "ERR_NO_INFORMATION":

                errorResource = R.string.error_no_information;

                break;

            case "INVALID_CARD_NUMBER":
                errorResource = R.string.error_invalid_card_no;
                break;

            case "ERR_OTP_VALIDATION_FAILED":
                errorResource = R.string.error_invalid_otp;
                break;

            case "ERR_INCORRECT_PIN":
                errorResource = R.string.error_invalid_pin;
                break;

            default:

                errorResource = R.string.error_operation_failed;

                break;
        }

        strError=context.getString(errorResource);


        return strError;
    }

}
