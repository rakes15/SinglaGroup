package whatsapp.autoresponder.Extractor;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

class b extends WhatsExtractor {
    b(Context context) {
        super(context);
    }

    private String getStyle(String str, CharSequence[] charSequenceArr, String str2) {
        if (str2 == null && str.matches("^.+\\s@\\s.+$")) {
            return str.split(" @ ", 2)[1];
        }
        if (str2 == null || str2.equals("android.app.Notification$BigTextStyle")) {
            return str;
        }
        if (!str2.equals("android.app.Notification$InboxStyle") || charSequenceArr == null) {
            return "";
        }
        String charSequence = charSequenceArr[charSequenceArr.length - 1].toString();
        if (!str.equals("WhatsApp")) {
            return str;
        }
        if (charSequence.matches("^.+\\s@\\s.+:\\s.+$")) {
            return charSequence.split(": ", 2)[0].split(" @ ", 2)[1];
        }
        return charSequence.split(": ", 2)[0];
    }

    private String getStyle(String str, String str2, CharSequence[] charSequenceArr, String str3) {
        if (str3 == null && str.matches("^.+\\s@\\s.+$")) {
            return str.split(" @ ", 2)[0] + ": " + str2;
        }
        if (str3 == null || str3.equals("android.app.Notification$BigTextStyle")) {
            return str2;
        }
        if (!str3.equals("android.app.Notification$InboxStyle") || charSequenceArr == null) {
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
                String string = bundle.getString(NotificationCompat.EXTRA_TITLE);
                CharSequence charSequence = bundle.getCharSequence(NotificationCompat.EXTRA_TEXT);
                String string2 = bundle.getString(NotificationCompat.EXTRA_TEMPLATE);
                String msgId = bundle.getString(NotificationCompat.getSortKey(notification));
                Log.i("Notification service","NotifyKey: b: "+msgId);
                if (!(string == null || charSequence == null)) {
                    CharSequence[] charSequenceArray = bundle.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES);
                    String style = getStyle(string, charSequenceArray, string2);
                    String stylrSecond = getStyle(string, charSequence.toString(), charSequenceArray, string2);
//                    boolean isStyle = getStyle(stylrSecond);
                    boolean isStyle = isGroup(stylrSecond);
                    if (!(style == null || stylrSecond == null || TextUtils.isEmpty(style) || TextUtils.isEmpty(stylrSecond))) {
                        receivedWhatsAppMessage = new ReceivedWhatsAppMessage(style, "", isStyle, stylrSecond, notification.when, getProfileImage(notification), string2 != null,"","1");
                        receivedWhatsAppMessage.setType(getProfileImage(receivedWhatsAppMessage));
                    }
                }
            }
        }
        return receivedWhatsAppMessage;
    }
}
