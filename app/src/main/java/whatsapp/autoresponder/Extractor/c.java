package whatsapp.autoresponder.Extractor;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import whatsapp.autoresponder.Utils.Preferences;

class c extends WhatsExtractor {
    c(Context context) {
        super(context);
    }

    private String getStyle(String str, String str2, CharSequence[] charSequenceArr, CharSequence charSequence) {
        if (str2 != null) {
            return str2;
        }
        if (charSequence != null) {
            if (charSequence.equals("android.app.Notification$BigTextStyle")) {
                return str;
            }
            if (charSequence.equals("android.app.Notification$InboxStyle") && charSequenceArr != null) {
                String charSequence2 = charSequenceArr[charSequenceArr.length - 1].toString();
                if (!str.equals("WhatsApp")) {
                    return str;
                }
                if (charSequence2.matches("^.+\\s@\\s.+:\\s.+$")) {
                    return charSequence2.split(": ", 2)[0].split(" @ ", 2)[1];
                }
                return charSequence2.split(": ", 2)[0];
            }
        }
        return null;
    }

    private String getStyleTwo(String str, String str2, CharSequence[] charSequenceArr, CharSequence charSequence) {
        if (charSequence.equals("android.app.Notification$BigTextStyle")) {
            return str2;
        }
        if (!charSequence.equals("android.app.Notification$InboxStyle") || charSequenceArr == null) {
            return "";
        }
        str2 = charSequenceArr[charSequenceArr.length - 1].toString();
        if (!str.equals("WhatsApp")) {
            return str2;
        }
        if (str2.matches("^.+\\s@\\s.+:\\s.+$")) {
            return str2.split(" @ ", 2)[0] + ": " + str2.split(": ", 2)[1];
        }
        return str2.split(": ", 2)[1];
    }

    private boolean getStyle(String str) {
        return str.matches("^.+:\\s.+");
    }

    private String[] GetFommatedStr(String str){
        String[] strings = new String[2];
        if(str.matches("^.+\\s(.+.).+:$")){  // +91 96601 90504? (2 messages):
            String temp = str.replaceAll("[\\D]", "");
            if (temp.length() > 10 && temp.length() < 15){
                temp = "+"+temp.substring(0,12);    // 919660190504
            }else{
                str = str.replace("(","$");            // My Number $2 messages):
                temp = str.split("$")[0];           // My Number
            }
            strings[0] = temp;
            strings[1] = "";
        }else if(str.matches("^.+:$")){     // My Number:
            String temp = str.replaceAll("[\\D]", "");
            if (temp.length() > 10 && temp.length() < 15){
                temp = "+"+temp.substring(0,12); // 919660190504
            }else{
                temp = str.split(":")[0];        // My Number
            }
            strings[0] = temp;
            strings[1] = "";
        }else if(str.matches("^.+:\\s.+$")){     // 3M Development Group: My Number
            strings[0] = str.split(":")[0];      // 3M Development Group
            strings[1] = str.split(":")[1];      // My Number
        }else if(str.matches("^.+\\s(.+.).+:\\s.+$")){     // 3M Development Group (4 messages): My Number
            if(str.contains("\u200B")){
                str = str.replace("(","$").split("$")[0];
                strings[0] = str;                // 3M Development Group
            }else {
                String temp = str.replace("(", "$");            // 3M Development Group $4 messages): My Number
                strings[0] = temp.split("$")[0];                // 3M Development Group
                strings[1] = str.split(":")[1];                // My Number
            }
        }else if(str.matches("^.+\\s@\\s.+$")){     // My Number @ 3M Development Group
            strings[0] = str.split("@")[1];         // 3M Development Group
            strings[1] = str.split("@")[0];         // My Number
        }else {
            strings[0] = str;
            strings[1] = "";
        }
        return strings;
    }
    private String[] GetFommatedText(String str){
        String[] strings = new String[2];
        if(str.matches("^.+.:.+$")){        // My Number: Testing
            String temp[] = str.split(":"); // Testing
            String s = "";
            for(int i = 0; i<temp.length; i++){
                if(i>0){
                    s += temp[i];
                }
            }
            strings[0] = str.split(":")[0]; // My Number
            strings[1] = s; // Testing
        }else if(str.matches("^.+.@.+.:.+$")){     // My Number @ Development: ðŸ“· Photo
            String temp[] = str.split(":"); // Testing
            String s = "";
            for(int i = 0; i<temp.length; i++){
                if(i>0){
                    s += temp[i];
                }
            }
            strings[0] = str.split("@")[0];        // My Number
            strings[1] = s;                        // ðŸ“· Photo
        }else if(str.matches("^.+.@.+.:.+.\\s(.*)$")){     // My Number @ Development: ðŸ“· Photo    testing
            String temp[] = str.split(":"); // Development
            String s = "";
            for(int i = 0; i<temp.length; i++){
                if(i>0){
                    s += temp[i];
                }
            }
            strings[0] = str.split("@")[0];        // My Number
            strings[1] = s;                        // ðŸ“· Photo
        }else {
            strings[0] = "";
            strings[1] = str;
        }
        return strings;
    }

    private boolean isGroup(String str) {
        if (str.contains(":"))
            if (str.contains("\u200B"))
                return false;
            else
                return str.matches("^.+:\\s.+$");
        else if (str.contains("@"))
            return str.matches("^.+@.+$");
        else
            return false;
    }

    @Nullable
    public ReceivedWhatsAppMessage extractWhatsAppMessage(Notification notification) {
        ReceivedWhatsAppMessage receivedWhatsAppMessage = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle bundle = notification.extras;
            if (bundle != null) {
                CharSequence[] charSequenceArray = bundle.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES);
                String string = bundle.getString(NotificationCompat.EXTRA_TITLE);

                if (string !=null && string.toLowerCase().trim().equals("whatsapp")) {
                    string = Preferences.getPreferenceString(mContext, Preferences.KEY_IN_LAST_TITLE, "");
                } else {
                    Preferences.setPreferenceString(mContext, Preferences.KEY_IN_LAST_TITLE, string);
                }

                CharSequence charSequence = bundle.getCharSequence(NotificationCompat.EXTRA_TEXT);
                CharSequence charSequence2 = bundle.getCharSequence(NotificationCompat.EXTRA_TEMPLATE);
//            String string2 = bundle.getString(NotificationCompat.EXTRA_SELF_DISPLAY_NAME);
                String string2 = bundle.getString("android.selfDisplayName");
                String msgId = bundle.getString(NotificationCompat.getSortKey(notification));
                //Log.i("Notification service","NotifyKey: c: "+msgId);
                if (!(string == null || charSequence2 == null || charSequence == null)) {
                    String style = getStyle(string, string2, charSequenceArray, charSequence2);
                    String styleTwo = getStyleTwo(string, charSequence.toString(), charSequenceArray, charSequence2);
                    boolean PmAsRCu4EZ37qeooPPW2 = isGroup(string);
                    if (!(style == null || styleTwo == null || TextUtils.isEmpty(style))) {

                        receivedWhatsAppMessage = new ReceivedWhatsAppMessage(GetFommatedStr(style)[0], GetFommatedStr(style)[1], PmAsRCu4EZ37qeooPPW2, GetFommatedText(styleTwo)[1], notification.when, getProfileImage(notification), !charSequence2.equals("android.app.Notification$MessagingStyle"), "", "1");
//                        receivedWhatsAppMessage = new ReceivedWhatsAppMessage(style, "", PmAsRCu4EZ37qeooPPW2, styleTwo, notification.when, getProfileImage(notification), !charSequence2.equals("android.app.Notification$MessagingStyle"), "", "1");
                        receivedWhatsAppMessage.setType(getProfileImage(receivedWhatsAppMessage));
                    }
                }
            }
        }
        return receivedWhatsAppMessage;
    }
}
