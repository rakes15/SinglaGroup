package whatsapp.autoresponder.Extractor;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.annotation.Nullable;
import android.util.Log;

import com.singlagroup.R;
import java.io.ByteArrayOutputStream;


public abstract class WhatsExtractor {
    protected Context mContext;

    public static class WhatsAppConstants {
        public static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    }

    @Nullable
    public abstract ReceivedWhatsAppMessage extractWhatsAppMessage(Notification notification);

    WhatsExtractor(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @SuppressWarnings("deprecation")
    byte[] getProfileImage(Notification notification) {
        Bitmap bitmap = notification.largeIcon;
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    ReceivedWhatsAppMessage.MessageType getProfileImage(ReceivedWhatsAppMessage receivedWhatsAppMessage) {
        try {
            int i = 0;
            String text = receivedWhatsAppMessage.getText();
            String[] stringArray = this.mContext.getResources().getStringArray(R.array.audio_type_sentences);
            if (receivedWhatsAppMessage.isfromGroup()) {
                text = text.isEmpty() ? "" : text.substring(text.indexOf(": ") + 2);
            }
            if (text.length() > 3) {
                if (text.substring(0, 2).matches("📷")) {
                    return ReceivedWhatsAppMessage.MessageType.IMAGE;
                }
                int length = stringArray.length;
                while (i < length) {
                    if (text.substring(3).matches(stringArray[i])) {
                        return ReceivedWhatsAppMessage.MessageType.AUDIO;
                    }
                    i++;
                }
            }
        }catch (Exception e){
            Log.e("WhatsExtractor", "Exception:"+e.toString());
        }
        return ReceivedWhatsAppMessage.MessageType.TEXT;
    }
}
