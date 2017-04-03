package com.android.sms.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Raju on 11/21/2016.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    private static String OTP_MESSAGE_CONTENT = "Otp : (.*)";

    private String otpCode = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle data = intent.getExtras();

        if(data!=null) {

            try {

                Object[] pdus = (Object[]) data.get("pdus");

                for (Object pdu : pdus) {

                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);

                    String sender = smsMessage.getDisplayOriginatingAddress();
                    //You must check here if the sender is your provider and not another one with same text.

                    String messageBody = smsMessage.getMessageBody();

                    Pattern generalOtpPattern = Pattern.compile(OTP_MESSAGE_CONTENT);
                    Matcher generalOtpMatcher = generalOtpPattern.matcher(messageBody);

                    if (generalOtpMatcher.find()) {

                        otpCode = generalOtpMatcher.group(1);
                    }

                    //pass on the text to out listener
                    mListener.messageReceived(otpCode);
                }
            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }

    public static void bindListener(SmsListener smsListener){

        mListener = smsListener;
    }
}
