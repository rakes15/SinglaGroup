package com.singlagroup.otpverification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
        
        private static SmsListener mListener;

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data  = intent.getExtras();
            try {
                Object[] pdus = (Object[]) data.get("pdus");

                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    String sender = smsMessage.getDisplayOriginatingAddress();
                    //You must check here if the sender is your provider and not another one with same text.

                    String messageBody = smsMessage.getDisplayMessageBody();
                    String str = "";
                    Pattern pattern = Pattern.compile("\\w+([0-9])");
                    Matcher matcher = pattern.matcher(messageBody);
                    for (int j = 0; j < matcher.groupCount(); j++) {
                        matcher.find();
                        Log.e("No....--", matcher.group());
                        if (!matcher.group().equals("")) {
                            str = matcher.group();
                        }
                    }
                    //Pass on the text to our listener.
                    if (!str.isEmpty() && str != null)
                        mListener.messageReceived(str);
                }
            }catch (Exception e){
                Log.e("Sms Listner","Exception:"+e.toString());
            }

        }

        public static void bindListener(SmsListener listener) {
            mListener = listener;
        }
    }