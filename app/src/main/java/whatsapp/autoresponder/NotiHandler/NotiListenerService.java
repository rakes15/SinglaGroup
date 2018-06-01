package whatsapp.autoresponder.NotiHandler;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.util.Base64;
import android.util.Log;
import com.singlagroup.MainActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import whatsapp.autoresponder.Api.RestApi;
import whatsapp.autoresponder.Extractor.Manager.MessageSender;
import whatsapp.autoresponder.Extractor.Manager.NotificationWearr;
import whatsapp.autoresponder.Extractor.Manager.NotificationsWearManager;
import whatsapp.autoresponder.Extractor.ReceivedWhatsAppMessage;
import whatsapp.autoresponder.Extractor.WhatsExtractor;
import whatsapp.autoresponder.Extractor.WhatsExtractorFactory;
import whatsapp.autoresponder.Model.Rest.ListResponse;
import whatsapp.autoresponder.Model.SchedulerModel;
import whatsapp.autoresponder.Service.SchedulerService;
import whatsapp.autoresponder.Utils.Global;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import whatsapp.database.DBSqliteWhatsApp;

public class NotiListenerService extends NotificationListenerService {

    private Context context;
    private WhatsExtractor whatsExtractor;
    private NotificationsWearManager notificationsWearManager;
    private RestApi api;
    private int fType=0;
    private String fName = "";
    private String DeviceID = "";
    private byte[] byteExtra;
    private DBSqliteWhatsApp DBwhatsApp;
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        DBwhatsApp = new DBSqliteWhatsApp(getApplicationContext());

        api = Global.initRetrofit(context);

        this.whatsExtractor = new WhatsExtractorFactory().getWhatsExtractor(this, Build.VERSION.SDK_INT);
        this.notificationsWearManager = NotificationsWearManager.getInstance();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        byteExtra = ExtraNotication(sbn);
        String pack = sbn.getPackageName();

        if (!pack.equals(getString(R.string.packname))) {
            return;
        }

        long postTime;
        if (Build.VERSION.SDK_INT > 23) {
            postTime = sbn.getPostTime();
        } else {
            postTime = sbn.getNotification().when;
        }

        Notification notification = sbn.getNotification();
        //TODO: Get Image
        Log.e("Received Notification","Notify: All=====->  "+notification.extras.getString(NotificationCompat.EXTRA_TITLE));

        ReceivedWhatsAppMessage extractWhatsAppMessage = this.whatsExtractor.extractWhatsAppMessage(notification);

        if (extractWhatsAppMessage != null) {
            extractWhatsAppMessage.setTime(postTime);
            NotificationWearr notificationWearr = notificationWearr(sbn);
            notificationWearr.setIdentifier(extractWhatsAppMessage.getConverName());
            this.notificationsWearManager.addNotificationWear(notificationWearr);

            if (!extractWhatsAppMessage.isRelevant()) {
                return;
            }

            try {
                Log.e("Iteretor", "KEY :  " + extractWhatsAppMessage.getConverName() + "   VALUE : " + extractWhatsAppMessage.getText());
                sendMessages(extractWhatsAppMessage,notification);
                return;
            } catch (Exception e) {
                Log.e("Notification Service","Exception:"+e.toString());
            }
            return;
        }
        return;
    }

    private void sendMessages(final ReceivedWhatsAppMessage extractWhatsAppMessage,Notification notification) {

        final String converName = extractWhatsAppMessage.getConverName();
        final String phnNumber = extractWhatsAppMessage.getNumber();
        final String text = extractWhatsAppMessage.getText();
        byte[] byteProImg = extractWhatsAppMessage.getProfileImage();
        final String profileImage = byteProImg != null ? Base64.encodeToString(byteProImg, Base64.DEFAULT) : "";
        final String time = "" + extractWhatsAppMessage.getTime();

        final String isRelevant = extractWhatsAppMessage.isRelevant() ? "1" : "0";
        final String isFromGroup = extractWhatsAppMessage.isfromGroup() ? "1" : "0";
        final String messageId = extractWhatsAppMessage.getMessageId() == null ? "" : extractWhatsAppMessage.getMessageId();
        final String activityType = extractWhatsAppMessage.getActivityType() == null ? "0" : extractWhatsAppMessage.getActivityType();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Run your task here
                DeviceID = new MainActivity().GetSharePreferenceDeviceInfo(context).getAndroidDUID() == null ? "" : new MainActivity().GetSharePreferenceDeviceInfo(context).getAndroidDUID();
            }
        }, 1000 );
        //byte[] byteNoty = GetFileByNotification(notification,extractWhatsAppMessage.getText());
        final String FileName = converName+".png";//fName;
        final String FileType = ""+fType;
        final String image = "";//byteExtra != null ? Base64.encodeToString(byteExtra, Base64.DEFAULT) : "";

        // Store incoming msg to Database Sqlite
        IncomingMsgStoreInDB(converName,phnNumber,  isFromGroup, activityType, text, time, FileName, profileImage, DeviceID);
    }

    private void IncomingMsgStoreInDB(String converName,String phnNumber, String isFromGroup,String activityType, String text, String time,String FileName, String profileImage,String DeviceID){
        // Scheduler Model
        SchedulerModel schedulerModel = new SchedulerModel();
        schedulerModel.setConversationName(converName);
        schedulerModel.setPhnNumber(phnNumber);
        schedulerModel.setIsFromGroup(isFromGroup);
        schedulerModel.setMessageType(SchedulerService.TYPE_TEXT);
        schedulerModel.setActivityType(activityType);
        schedulerModel.setText(text);
        schedulerModel.setTime(time);
        schedulerModel.setFileName(FileName);
        schedulerModel.setFileUrl(profileImage);
        schedulerModel.setDeviceId(DeviceID);
        // insert data into db
        DBwhatsApp.insertWhatsAppTable(schedulerModel, DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG);

        // TODO: Call to find Whats app contact
        if (!DBwhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0).isEmpty()) {

            for (int i = 0; i < DBwhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0).size(); i++) {
                if(DBwhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0).get(i).getIsFromGroup().equals("0")) {
                    //TODO : isGroup 0 Call Get Whats App contact
                    GetWhatsAppContact(DBwhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0).get(i));

                }else if(DBwhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0).get(i).getIsFromGroup().equals("1")) {
                    //TODO : isGroup 1 Send Group Message to the server
                    SchedulerModel schModel = DBwhatsApp.GetInOutDataByTypeWithFlag(DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_0).get(i);
                    schModel.setIsFromGroup("1");
                    if (schModel.getConversationName().contains("(")){
                        String temp = schModel.getConversationName().replace("(",":");
                        schModel.setConversationName( temp.split(":")[0] );
                        Log.e("Notification" , "Name:"+schModel.getConversationName());
                    }
//                    Log.e("Notification" , "Name:"+schModel.getPhnNumber());
//                    Log.e("Notification" , "Number:"+schModel.getConversationName());
                    storeIncomingMsg( schModel.getDeviceId(), schModel.getConversationName(), schModel.getPhnNumber(), schModel.getProfileImage(), schModel.getText(), schModel.getTime(), schModel.getIsFromGroup(), schModel.getId());
                }
            }

        }
    }

    public void GetWhatsAppContact(final SchedulerModel schedulerModel){

        //TODO: Async Task for Whats app contact searching
        new AsyncTask<Void, Void, String>() {
            boolean isNumber = false;
            @Override
            protected String doInBackground(Void... voids) {
                String PhoneNumber = GetWANumberFromName(schedulerModel.getConversationName());
                if (!PhoneNumber.equals(schedulerModel.getConversationName())){
                    isNumber = true;
                }
                return GetWANumberFromName(schedulerModel.getConversationName());
            }

            @Override
            protected void onPostExecute(String phnNumber) {
                super.onPostExecute(phnNumber);
                try {
                    if (isNumber){
                        // TODO: Contact found from Whats App
                        phnNumber = phnNumber.replaceAll("[\\D]", "");
                        if (schedulerModel.getConversationName().contains("(")){
                            String temp = schedulerModel.getConversationName().replace("(",":");
                            schedulerModel.setConversationName( temp.split(":")[0] );
                            Log.e("Notification" , "Name:"+schedulerModel.getConversationName());
                        }
                        Log.e("Notification" , "Phone:"+phnNumber +"\tName:"+schedulerModel.getConversationName());
                        storeIncomingMsg(schedulerModel.getDeviceId(),"+"+phnNumber, schedulerModel.getConversationName(), schedulerModel.getProfileImage(), schedulerModel.getText(), schedulerModel.getTime(), schedulerModel.getIsFromGroup(),schedulerModel.getId());
                    }else {
                        String phone = phnNumber.replaceAll("[\\D]", "");
                        if (phone.length() >= 10 && phone.length() < 14) {
                            phnNumber = phone; //phnNumber.replaceAll("[\\D]", "");
                            phnNumber = "+"+phnNumber;
                            //int length = phnNumber.length();
                            //phnNumber = phnNumber.substring(length - 10);
                            storeIncomingMsg(schedulerModel.getDeviceId(), phnNumber, schedulerModel.getConversationName(), schedulerModel.getProfileImage(), schedulerModel.getText(), schedulerModel.getTime(), schedulerModel.getIsFromGroup(), schedulerModel.getId());
                        }else {
                            schedulerModel.setIsFromGroup("1");
                            if (schedulerModel.getConversationName().contains("(")){
                                String temp = schedulerModel.getConversationName().replace("(",":");
                                schedulerModel.setConversationName( temp.split(":")[0] );
                                Log.e("Notification" , "Name:"+schedulerModel.getConversationName());
                            }
                            storeIncomingMsg(schedulerModel.getDeviceId(), phnNumber, schedulerModel.getConversationName(), schedulerModel.getProfileImage(), schedulerModel.getText(), schedulerModel.getTime(), schedulerModel.getIsFromGroup(), schedulerModel.getId());
                        }
                    }
                } catch (Exception e) {
                    Log.e("NotiListenerService", "Exception:" + e.toString());
                }
            }
        }.execute();
    }

    private void storeIncomingMsg(String DeviceID, String phnNumber, String converName, String profileImage, String text, String time, String isFromGroup, final String Id) {
        if (Global.isNetworkAvailable(context)) {

            Call<ListResponse<String>> listResponseCall = api.storeIncomingMsg(
                    DeviceID,
                    converName,
                    phnNumber,
                    profileImage,
                    text,
                    time,
                    isFromGroup,
                    Id
            );

            listResponseCall.enqueue(new Callback<ListResponse<String>>() {
                @Override
                public void onResponse(Call<ListResponse<String>> call, Response<ListResponse<String>> response) {
                    Log.e("NotiListenerService", "onResponse");
                    //Update Entry by Flag
                    DBwhatsApp.UpdateByFlag(Id, DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);
                    //Delete Entry by Flag
                    DBwhatsApp.DeleteByFlag(Id, DBSqliteWhatsApp.WHATS_APP_INCOMING_FLAG, DBSqliteWhatsApp.WHATS_APP_SEND_FLAG_1);
                }

                @Override
                public void onFailure(Call<ListResponse<String>> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("NotiListenerService", "onFailure");
                }
            });
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private NotificationWearr notificationWearr(StatusBarNotification statusBarNotification) {
        return new NotificationWearr(new WearableExtender(statusBarNotification.getNotification()));
    }

    public void sendMessageByNameAndMsg(String converName, String message) {
        new MessageSender(this).sendMessage(converName, message);
    }

    private byte[] GetFileByNotification(Notification notification,String Title){
        byte[] result = null;
        if(Title.contains("\uD83D\uDCF7")){
            fType = 1;
            fName = DateFormatsMethods.getDateTime()+".png";
            //Photo
            Bitmap id = notification.largeIcon;
            if (id != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                id.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();
                result = stream.toByteArray();
                //msgrcv.putExtra("icon", byteArray);
                Log.e("Received Notification","Image:"+result);
                String res = String.valueOf(result);
                Log.e("Received Notification","String Image:"+res);
                Log.e("Received Notification","Byte Image:"+ res.getBytes());
            }
        }else if(Title.contains("\uD83D\uDCC4")){
            fType = 2;
            fName = DateFormatsMethods.getDateTime()+".pdf";
            //Document
            Bitmap id = notification.largeIcon;
            if (id != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                id.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();
                result = stream.toByteArray();
                //msgrcv.putExtra("icon", byteArray);
                Log.i("Received Notification", "Document:" + result);
            }
        }else if(Title.contains("\uD83C\uDFA5")){
            fType = 3;
            fName = DateFormatsMethods.getDateTime()+".mp4";
            //Video
            Bitmap id = notification.largeIcon;
            if (id != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                id.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();
                result = stream.toByteArray();
                //msgrcv.putExtra("icon", byteArray);
                Log.i("Received Notification","Video:"+result);
            }
        }else if(Title.contains("\uD83C\uDFB5")){
            fType = 4;
            fName = DateFormatsMethods.getDateTime()+".mp3";
            //Audio
            Log.i("Received Notification","Audio:"+notification.extras.get(Notification.EXTRA_AUDIO_CONTENTS_URI));
            Bitmap id = notification.largeIcon;
            if (id != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                id.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();
                result = stream.toByteArray();
                //msgrcv.putExtra("icon", byteArray);
                Log.i("Received Notification","Audio:"+result);
            }
        }
        return result;
    }

    private byte[] ExtraNotication(StatusBarNotification statusBarNotification){
        byte[] result=null;
        // a notification is posted

        String pack = statusBarNotification.getPackageName();

        Bundle extras = statusBarNotification.getNotification().extras;
        extras.get(Notification.EXTRA_LARGE_ICON_BIG);
        int iconId = extras.getInt(Notification.EXTRA_SMALL_ICON);

        try {
            PackageManager manager = getPackageManager();
            Resources resources = manager.getResourcesForApplication(pack);

            Drawable icon = resources.getDrawable(iconId);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (extras.containsKey(Notification.EXTRA_LARGE_ICON)) {
            // this bitmap contain the picture attachment
            Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);//get(Notification.EXTRA_PICTURE);
            if (bmp != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();
                result = stream.toByteArray();
                //msgrcv.putExtra("icon", byteArray);
                Log.e("Received Notification","Image:"+result);
            }
        }
        return result;
    }

    private String GetWANumberFromName(String WAName) {

        String phoneNo = WAName;

        final String[] projection = {
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.MIMETYPE,
                "account_type",
                ContactsContract.Data.DATA3,
        };

        final String selection = ContactsContract.Data.MIMETYPE + " =? and account_type=?";
        final String[] selectionArgs = {
                "vnd.android.cursor.item/vnd.com.whatsapp.profile",
                "com.whatsapp"
        };

        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        while (c.moveToNext()) {
            String id = c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
            String number = c.getString(c.getColumnIndex(ContactsContract.Data.DATA3));
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if (WAName.contains(name)) {
                phoneNo = number;
                return phoneNo;
            }
            //Log.e("WhatsApp", "name " +name + " - number - "+number);
        }
        c.close();

        return phoneNo;
    }
}