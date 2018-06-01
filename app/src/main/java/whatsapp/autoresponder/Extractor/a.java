package whatsapp.autoresponder.Extractor;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

class a extends WhatsExtractor {
    a(Context context) {
        super(context);
    }

    private String PmAsRCu4EZ37qeooPPW(String str, CharSequence[] charSequenceArr) {
        if (!str.equals("WhatsApp") || charSequenceArr == null) {
            return str;
        }
        String charSequence = charSequenceArr[charSequenceArr.length - 1].toString();
        if (charSequence.matches("^.+\\s@\\s.+:\\s.+$")) {
            return charSequence.split(": ", 2)[0].split(" @ ", 2)[1];
        }
        return charSequence.split(": ", 2)[0];
    }

    private String PmAsRCu4EZ37qeooPPW(String str, String str2, CharSequence[] charSequenceArr) {
        if (str2 != null || charSequenceArr == null) {
            return str2;
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

    private boolean PmAsRCu4EZ37qeooPPW(String str) {
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
        String str = null;
        ReceivedWhatsAppMessage receivedWhatsAppMessage=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Bundle bundle = notification.extras;
            if (bundle == null) {
                return null;
            }
            String string = bundle.getString(NotificationCompat.EXTRA_TITLE);
            CharSequence charSequence = bundle.getCharSequence(NotificationCompat.EXTRA_TEXT);
            CharSequence[] charSequenceArray = bundle.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES);
            String msgId = bundle.getString(NotificationCompat.getSortKey(notification));
            Log.i("Notification service","NotifyKey: a: "+msgId);
            if (string == null) {
                return null;
            }
            if (charSequence == null && charSequenceArray == null) {
                return null;
            }
            String PmAsRCu4EZ37qeooPPW = PmAsRCu4EZ37qeooPPW(string, charSequenceArray);
            if (charSequence != null) {
                str = charSequence.toString();
            }
            String PmAsRCu4EZ37qeooPPW2 = PmAsRCu4EZ37qeooPPW(string, str, charSequenceArray);
            //receivedWhatsAppMessage = new ReceivedWhatsAppMessage(PmAsRCu4EZ37qeooPPW, "", PmAsRCu4EZ37qeooPPW(PmAsRCu4EZ37qeooPPW2), PmAsRCu4EZ37qeooPPW2, notification.when, getProfileImage(notification), true, "", "1");
            receivedWhatsAppMessage = new ReceivedWhatsAppMessage(PmAsRCu4EZ37qeooPPW, "", isGroup(PmAsRCu4EZ37qeooPPW2), PmAsRCu4EZ37qeooPPW2, notification.when, getProfileImage(notification), true, "", "1");
            receivedWhatsAppMessage.setType(getProfileImage(receivedWhatsAppMessage));
        }
        return receivedWhatsAppMessage;
    }
}
