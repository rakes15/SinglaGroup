package com.singlagroup.customwidgets;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;

/**
 * Created by rakes on 12-Jul-17.
 */

public class SendToOtherApps {

    //TODO: Send SMS
    public static void sendSMS(Context context,String Mobile) {
        Log.i("Send SMS", "");
        try {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("smsto:"));
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address"  , new String (Mobile));
            smsIntent.putExtra("sms_body"  , "Hello Sms ");
            context.startActivity(smsIntent);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            MessageDialog.MessageDialog(context,"Exception","SMS faild, please try again later.");
        }
    }
    //TODO: Send SMS
    public static void sendEmail(Context context,String Email) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setType("plain/text");
            emailIntent.setData(Uri.parse("mailto:" + Email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Account Info");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello mail");
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            MessageDialog.MessageDialog(context,"Exception","Mail failed, please try again later.");
        }
    }
    //TODO: Send Whats App
    public static void sendWhatsAppNumber(Context context,String Mobile,String Message) {
        try {
//            Intent sendIntent = new Intent("android.intent.action.MAIN");
//            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.setType("text/plain");
//            sendIntent.putExtra(Intent.EXTRA_TEXT, Message);
//            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(Mobile) + "@s.whatsapp.net");//phone number without "+" prefix
//            context.startActivity(sendIntent);


        } catch(Exception e) {
            MessageDialog.MessageDialog(context,"","Whats App is not installed");
        }
    }
    //TODO: Send Whats App List
    public static void sendWhatsAppList(Context context, String Message) {
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, Message);
            sendIntent.setPackage("com.whatsapp");
            context.startActivity(sendIntent);
        } catch(Exception e) {
            MessageDialog.MessageDialog(context,"Send whats app Exception",""+e.toString());
        }
    }
}
