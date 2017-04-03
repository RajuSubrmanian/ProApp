package com.inspirenetz.app.proapp.customer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.UserInfoResource;

/**
 * Created by Raju on 11/22/2016.
 */

public class ShowCustomerQRCodeFragment extends Fragment {

    private View rootView;

    private LayoutInflater currInflater;

    private Context context;

    private ImageView imgShowQRCode;

    SessionManager sessionManager;

    String cusLoyaltyId = "";

    // keep merchant no
    private String qrCodeData = "";

    private Dialog dlgProgress;

    Bitmap bmp;

    //Create a object for Handler class
    final Handler mHandler = new Handler();

    private GeneralMethods generalMethods = new GeneralMethods();

    //Runnable instance for mGetRedemptionDetails
    final Runnable mShowQrCode = new Runnable() {
        @Override
        public void run() {

            updateQRCodeGeneration();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get context
        context = getActivity();

        // Get Inflator
        currInflater = inflater;

        // Create the Session Manager object
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        // Check the login
        sessionManager.checkLogin();

        // Create the GeneralMethod object
        GeneralMethods genMethod = new GeneralMethods();

        // Check if the network connection is available
        boolean isConnected = genMethod.isOnline(context);

        // If the connection not available
        // then show the toast message
        if (!isConnected) {

            genMethod.showToastMessage(context, "Network Unavailable");

        }

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_show_qr_code, container, false);

        initialize();

        showQRCode();

        // Get Arguments
        Bundle params = getArguments();

        if(params!=null){

            //setParams(params);

        }

        // Return root view
        return rootView;

    }

    /**
     * Method to initialize ui components
     */
    public void initialize(){

        // initialize qr code image view
        imgShowQRCode = (ImageView)rootView.findViewById(R.id.imgShowQRCode);

        UserInfoResource userInfoResource = sessionManager.getUserInfo();
        cusLoyaltyId = userInfoResource.getUsrLoginId();

    }

    /**
     * method to show the qr code
     */
    public void showQRCode(){

        dlgProgress = generalMethods.getCustomPogressDialog(context, "QR Code", "Generating...");

        dlgProgress.show();

        Thread thread = new Thread(){

            public void run(){

                try {

                    generateQrCode();


                }catch (Exception e){

                    Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
                }

                // Post to the mGetRedemptionDetails runnable
                mHandler.post(mShowQrCode);
            }

        };
        thread.start();

    }

    public void updateQRCodeGeneration(){

        imgShowQRCode.setImageBitmap(bmp);

        dlgProgress.dismiss();

    }

    /**
     * Method to generate the QR code
     */
    public void generateQrCode(){

        qrCodeData = "14"+"#"+cusLoyaltyId;

        // generate qr code
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrCodeData, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
