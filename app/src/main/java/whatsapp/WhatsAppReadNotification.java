package whatsapp;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.service.notification.NotificationListenerService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;

import java.io.ByteArrayOutputStream;
/**
 * Created by rakes on 28-Nov-17.
 */

public class WhatsAppReadNotification extends NotificationListenerService {

    Context context;
    private static String TAG = WhatsAppReadNotification.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.i(TAG,"Notification");
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String pack = sbn.getPackageName();
            String ticker = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = sbn.getNotification().extras;
            if (extras != null) {
                String title = extras.getString("android.title") == null ? "" : extras.getString("android.title");
                String text = extras.getString("android.text") == null ? "" : extras.getString("android.text");
                String PostTime = (sbn.getPostTime() == 0 ? "" : DateFormatsMethods.convertDate(String.valueOf(sbn.getPostTime()), "dd-MM-yyyy hh:mm:ss"));
                //UserHandle userHandle = (sbn.getUser() == null ? null : sbn.getUser());
                int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
                Bitmap id = sbn.getNotification().largeIcon;
                Log.i("Package", pack);
                Log.i("Ticker", ticker);
                Log.i("Title", title);
                Log.i("Text", text);

                Intent msgrcv = new Intent("Msg");
                msgrcv.putExtra("package", pack);
                msgrcv.putExtra("ticker", ticker);
                msgrcv.putExtra("title", title + "\t PostTime :" + PostTime);
                msgrcv.putExtra("text", text);// + " " + (userHandle == null ? "" : userHandle.toString()));
                msgrcv.putExtra("extras", extras);
                msgrcv.putExtra("PostTime", PostTime);
                if (id != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    id.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    msgrcv.putExtra("icon", byteArray);
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                 //Toast.makeText(context,""+title+"\nText:"+text,Toast.LENGTH_LONG).show();
                Log.i(TAG, "Notification:" + title + "\nText:" + text);
                //ShareOnWhatsApp(context,msgrcv);
            }
        }
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"Notification Removed");
    }
}